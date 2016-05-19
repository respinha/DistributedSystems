package pt.ua.sd.ropegame.memregions;

import genclass.GenericIO;
import pt.ua.sd.ropegame.entities.Coach;
import pt.ua.sd.ropegame.entities.CoachStrategies;
import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.enums.CoachState;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.enums.RefereeState;
import pt.ua.sd.ropegame.interfaces.*;
import pt.ua.sd.ropegame.utils.GameOfTheRopeConfigs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Memory region which stores information on both teams.
 */
public class Bench implements ICoachBench, IContestantsBench, IRefBench {

    private static final Random RANDOMGEN = new Random();

    // number of coaches
    private int nCoaches;

    private IBenchGenRep repository;

    // contestants and coaches
    Contestant[][] contestants;
    Coach[] coaches;

    private GeneralRepository gr;

    private boolean[] coachPickedContestants;

    // number of contestants in bench
    private int numTeamContestants[];

    // variable to assure that coaches go from WFRC to ASTE
    private int nCoachesBeingCalled;

    private int currentTrial;

    private Condition[] waitingForTeamContestants;

    private Lock mutex;                     // assures mutual exclusion
    private Condition[][] waitingForPick;
    private Condition waitingForTrialToStart;
    private boolean gameIsOver;

    private int currentGame;

    private int[][] pickedContestants;
    private boolean[][] wasPicked;
    private boolean contestantsHaveMoreOperations, coachesHaveMoreOperations;
    private int strengths[][];

    private static final int RESET_POSITION = -1;
    private int[] nContestants;

    /**
     *  Constructor for the bench.
     *  @param rep The @GeneralRepository interface containing all needed methods.
     */
    public Bench(IBenchGenRep rep) {

        mutex = new ReentrantLock();

        gameIsOver = false;

        waitingForTeamContestants = new Condition[2];
        waitingForTeamContestants[0] = mutex.newCondition();
        waitingForTeamContestants[1] = mutex.newCondition();


        waitingForPick = new Condition[2][5];
        for(int i = 0; i < waitingForPick.length; i++)
            for(int j = 0; j < waitingForPick[i].length; j++)
                waitingForPick[i][j] = mutex.newCondition();

        numTeamContestants = new int[2];
        numTeamContestants[0] = numTeamContestants[1] = 0;    // all contestants in bench at the start of the game

        repository = rep;

        nCoachesBeingCalled = 0;
        currentTrial = 0;

        nCoaches = 0;

        waitingForTrialToStart = mutex.newCondition();

        coachPickedContestants = new boolean[2];
        coachPickedContestants[0] = false;
        coachPickedContestants[1] = false;

        currentGame = 0;

        pickedContestants = new int[GameOfTheRopeConfigs.N_TEAMS][GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND/2];

        for(int[] row: pickedContestants)
            Arrays.fill(row, RESET_POSITION);
        wasPicked = new boolean[GameOfTheRopeConfigs.N_TEAMS][GameOfTheRopeConfigs.N_TEAM_CONTESTANTS];
        strengths = new int[GameOfTheRopeConfigs.N_TEAMS][GameOfTheRopeConfigs.N_TEAM_CONTESTANTS];

        coachesHaveMoreOperations = contestantsHaveMoreOperations = true;
        nContestants = new int[GameOfTheRopeConfigs.N_TEAMS];
        Arrays.fill(nContestants, 0);
    }

    /**
     * Called by referee to signal a new trial call.
     */
    @Override
    public void callTrial(Referee referee) {
        mutex.lock();

        if(nCoachesBeingCalled == 3)
            nCoachesBeingCalled = 0;

        nCoachesBeingCalled++;
        waitingForTrialToStart.signalAll();
        repository.updateRefState(RefereeState.TEAMS_READY.shortName());
        referee.changeState(RefereeState.TEAMS_READY);

        mutex.unlock();

    }

    /**
     * The coach is blocked in state WAITING_FOR_REFEREE_COMMAND.

     * @throws InterruptedException The wait was interrupted.
     */

    @Override
    public void waitForCoachCall() throws InterruptedException {

        mutex.lock();

        nCoachesBeingCalled++;
        while(nCoachesBeingCalled < GameOfTheRopeConfigs.N_COACHES+1)
            waitingForTrialToStart.await();
        waitingForTrialToStart.signalAll();

        mutex.unlock();

    }

    @Override
    public boolean coachesHaveMoreOperations() {
        return coachesHaveMoreOperations;
    }





    /**
     * Updates this coaches' team players strengths.
     * @param teamID
     * @param trial Boolean that signals if we're in the start of a game.
     * @param knockout
     */
    @Override
    public void reviewNotes(Coach coach, int teamID, int trial, boolean knockout) {
        mutex.lock();

        try {
            GenericIO.writelnString("NUMERO DE TRIALS: " + trial);
            nCoaches++; // increment the number of coaches reviewing notes

            // reset coaches waiting for coach call
            if (nCoaches == 1) {
                if(nCoachesBeingCalled == 3)
                    nCoachesBeingCalled = 0;
                if (trial == GameOfTheRopeConfigs.MAX_TRIALS+1 || knockout) {
                    currentTrial = 1;

                    // if we are on the first trial, we are at the beggining of a new game
                    currentGame++;
                } else currentTrial = trial;
            }

            if (nCoaches == GameOfTheRopeConfigs.N_COACHES)
                nCoaches = 0;   // reset number of coaches reviewing notes

            // stop the coach's lifecycle if we've reached MAX_GAMES + 1
            if (currentGame == GameOfTheRopeConfigs.MAX_GAMES) {
                coachesHaveMoreOperations = false;
                //return;
            }


            if(currentTrial != 1) {
                for (int j = 0; j < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; j++) {
                    strengths[teamID][j]++;

                    for (int k = 0; k < pickedContestants[teamID].length; k++) {
                        if (pickedContestants[teamID][k] == j) {

                            strengths[teamID][j] -= 2;
                            if (strengths[teamID][j] < 0) strengths[teamID][j] = 0;

                            pickedContestants[teamID][k] = RESET_POSITION;
                        }

                    }

                }


                repository.updateStrengths(teamID, strengths[teamID]);
            }
            CoachState state = CoachState.WAIT_FOR_REFEREE_COMMAND;
            coach.changeState(state);
            repository.updateCoachState(state.shortName(), teamID);


        } finally {
            mutex.unlock();
        }
    }

    /**
     * Picks team contestants based on coach's current strategy.
     * @param teamID
     * @param strategy
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public void callContestants(Coach coach, int teamID, String strategy) throws InterruptedException {

        mutex.lock();


        int j = 0;
        if(strategy.equals(String.valueOf(CoachStrategies.Strategy.RANDOM.shortName()))) {
            Set<Integer> generated = new HashSet<>();
            while (generated.size() < 3)
            {
                Integer n = RANDOMGEN.nextInt(4);
                generated.add(n);
            }

            for(int number: generated) {
                for (int i = 0; i < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; i++) {
                    if (i == number) {
                        wasPicked[teamID][i] = true;
                        waitingForPick[teamID][i].signal();
                        pickedContestants[teamID][j++] = i;
                    }
                }
            }
        }
        else if(strategy.equals(CoachStrategies.Strategy.PICK_FIRST_THREE_CONTESTANTS.shortName())) {
            for(int i = 0; i < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; i++) {
                if(i <= 2) {
                    wasPicked[teamID][i] = true;
                    waitingForPick[teamID][i].signal();
                    pickedContestants[teamID][j++] = i;
                } else break;
            }
        } else { // last three
            for(int i = 0; i < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; i++) {
                if(i >= 2) {
                    wasPicked[teamID][i] = true;
                    waitingForPick[teamID][i].signal();
                    pickedContestants[teamID][j++] = i;
                }
            }

        }



        coach.changeState(CoachState.ASSEMBLE_TEAM);
        repository.updateCoachState(CoachState.ASSEMBLE_TEAM.shortName(), teamID);

        mutex.unlock();

    }

    /**
     * Checks to see if a contestant was picked by his coach.
     * @return current trial number
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public int waitForContestantCall(int gameMemberID, int teamID) throws InterruptedException {

        mutex.lock();

        try {

            while(!wasPicked[teamID][gameMemberID])
                waitingForPick[teamID][gameMemberID].await();

            wasPicked[teamID][gameMemberID] = false;
            //contestant.callContestant(false);

            if(gameIsOver) return GameOfTheRopeConfigs.MAX_TRIALS + 1;

            return strengths[teamID][gameMemberID];

        } finally {
            mutex.unlock();
        }
    }

    /**
     * Called by the referee when a game is over to wake up contestants blocked at waitingForContestantCall().
     */
    @Override
    public void notifyContestantsMatchIsOver()  {

        mutex.lock();

        gameIsOver = true;

        for(int i = 0; i < GameOfTheRopeConfigs.N_TEAMS; i++) {
            for (int j = 0; j < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; j++)  {
                // c.callContestant(true);
                wasPicked[i][j] = true;
                waitingForPick[i][j].signalAll();
                contestantsHaveMoreOperations = false;
                //coachesHaveMoreOperations = false;
                // c.hasNoMoreOperations();
            }
        }


        mutex.unlock();
    }


    /**
     * Transition.
     */
    @Override
    public void seatDown(Contestant contestant, int gameMemberID, int teamID, int strength, int position) {
        mutex.lock();
        try {

            // contestant.removePlaygroundPosition();
            if(nContestants[teamID] < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS) {
                nContestants[teamID]++;
                this.assignStrength(teamID, gameMemberID, strength);
            }
            else {
                repository.removeContestantFromPosition(teamID, position);
            }
            repository.updateContestantState(ContestantState.SEAT_AT_THE_BENCH.shortName(), gameMemberID, teamID);
            contestant.changeState(ContestantState.SEAT_AT_THE_BENCH);

        } finally {
            mutex.unlock();
        }
    }

    private void assignStrength(int teamID, int gameMemberID, int strength) {

        strengths[teamID][gameMemberID] = strength;
        if(nContestants[teamID] == GameOfTheRopeConfigs.N_TEAM_CONTESTANTS) {
            repository.updateStrengths(teamID, strengths[teamID]);
            // repository.updateContestantState(ContestantState.SEAT_AT_THE_BENCH.shortName(), gameMemberID, teamID);
        }
    }

    @Override
    public boolean contestantsHaveMoreOperations() {
        return this.contestantsHaveMoreOperations;
    }


    /**
     * Assigns contestants to this bench.
     * @param contestants The two teams.
     */
    public void assignContestants(Contestant[][] contestants){
        mutex.lock();
       /* this.contestants = contestants;
        for(Contestant team[]: contestants)
            for(Contestant c: team)
                strengths[c.getTeam()][c.getNumber()] = c.getStrength();*/
        mutex.unlock();
    }

    /**
     * Assigns coaches to this bench.
     * @param coaches The coaches for both teams.
     */
    public void assignCoaches(Coach[] coaches){
        mutex.lock();
        this.coaches = coaches;
        mutex.unlock();
    }

}
