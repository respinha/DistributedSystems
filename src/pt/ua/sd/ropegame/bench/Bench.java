package pt.ua.sd.ropegame.bench;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.Response;
import pt.ua.sd.ropegame.common.enums.CoachStrategy;
import pt.ua.sd.ropegame.common.interfaces.IBenchGenRep;
import pt.ua.sd.ropegame.common.interfaces.IContestantsBench;
import pt.ua.sd.ropegame.common.interfaces.IRefBench;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.ICoachBench;

import java.rmi.RemoteException;
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
class Bench implements ICoachBench, IContestantsBench, IRefBench {

    private static final Random RANDOMGEN = new Random();

    // number of coaches
    private int nCoaches;

    // reference to a General Repository
    private IBenchGenRep repository;

    // variable to assure that coaches go from WFRC to ASTE
    private int nCoachesBeingCalled;

    // current trial
    private int currentTrial;
    private int currentGame;

    private int firstToEndMatch;


    private int[][] pickedContestants;
    private boolean[][] wasPicked;
    private boolean contestantsHaveMoreOperations, coachesHaveMoreOperations;
    private int strengths[][];
    private final int RESET_POSITION = -1;
    private int[] nContestants;

    private Lock mutex;                     // assures mutual exclusion

    // Conditions
    private Condition[][] waitingForPick;
    private Condition waitingForTrialToStart;

    private GameOfTheRopeConfigs configs;

    private int[] clocks = null;



    /**
    *  Constructor for the bench.
     *  @param rep The @GeneralRepository interface containing all needed methods.
     */
    public Bench(IBenchGenRep rep, GameOfTheRopeConfigs configs) {

        this.configs = configs;

        mutex = new ReentrantLock();

        firstToEndMatch = 0;


        waitingForPick = new Condition[2][5];
        for(int i = 0; i < waitingForPick.length; i++)
            for(int j = 0; j < waitingForPick[i].length; j++)
                waitingForPick[i][j] = mutex.newCondition();

        repository = rep;

        nCoachesBeingCalled = 0;
        currentTrial = 1;

        nCoaches = 0;

        waitingForTrialToStart = mutex.newCondition();

        currentGame = 0;

        pickedContestants = new int[configs.getNTeams()][configs.getMaxContsPlayground()/2];
        for(int[] row: pickedContestants)
            Arrays.fill(row, RESET_POSITION);

        wasPicked = new boolean[configs.getNTeams()][configs.getNContestants()];
        strengths = new int[configs.getNTeams()][configs.getNContestants()];

        coachesHaveMoreOperations = contestantsHaveMoreOperations = true;

        nContestants = new int[configs.getNTeams()];
        Arrays.fill(nContestants, 0);
    }

    /**
     * Called by referee to signal a new trial call.
     */
    @Override
    public Response callTrial() throws RemoteException {
        mutex.lock();

        try {
            if (nCoachesBeingCalled == 3) nCoachesBeingCalled = 0;

            nCoachesBeingCalled++;
            waitingForTrialToStart.signalAll();
            repository.updateRefState(RefereeState.TEAMS_READY.shortName());

            return new Response(clocks, RefereeState.TEAMS_READY.shortName());
        } finally {
            mutex.unlock();
        }

    }

    /**
     * The coach is blocked in state WAITING_FOR_REFEREE_COMMAND.

     * @throws InterruptedException The wait was interrupted.
     */

    @Override
    public Response waitForCoachCall() throws InterruptedException {
        mutex.lock();
        try {
            nCoachesBeingCalled++;
            while (nCoachesBeingCalled < configs.getNCoaches() + 1)
                waitingForTrialToStart.await();
            waitingForTrialToStart.signalAll();

            return new Response(clocks);
        } finally {
            mutex.unlock();
        }



    }

    /**
     * @return True if Coaches have more operations, False otherwise.
     */
    @Override
    public Response coachesHaveMoreOperations() {
        mutex.lock();

        try {
            return new Response(clocks, coachesHaveMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Updates a coach's team players strengths.
     * @param teamID The coach's team ID.
     * @param trial Current trial.
     * @param knockout True if knockout, false otherwise.
     */
    @Override
    public Response reviewNotes(int teamID, int trial, boolean knockout) throws RemoteException {
        mutex.lock();

        try {
            nCoaches++; // increment the number of coaches reviewing notes


            // reset coaches waiting for coach call
            if (nCoaches == 1) {
                if (nCoachesBeingCalled == 3)
                    nCoachesBeingCalled = 0;
                if (trial == configs.getMaxTrials()+1 || knockout) {
                    currentTrial = 1;

                    // if we are on the first trial, we are at the beggining of a new game
                    currentGame++;
                } else currentTrial = trial;
            }

            if (nCoaches == configs.getNCoaches())
                nCoaches = 0;   // reset number of coaches reviewing notes

            // stop the coach's lifecycle if we've reached MAX_GAMES + 1
            if (currentGame == configs.getMaxGames()) {
                coachesHaveMoreOperations = false;
                //return;
            }

            if (currentTrial != 1  || currentGame > 1) {
                for (int j = 0; j < configs.getNContestants(); j++) {
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
            repository.updateCoachState(state.shortName(), teamID);

            return new Response(null, state.shortName());
        } finally {
            mutex.unlock();
        }


    }

    /**
     * Picks team contestants based on coach's current strategy.
     * @param teamID The Coach's
     * @param strategy
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public Response callContestants(int teamID, String strategy) throws RemoteException {

        mutex.lock();

        try {

            int j = 0;
            if (strategy.equals(String.valueOf(CoachStrategy.Strategy.RANDOM.shortName()))) {
                Set<Integer> generated = new HashSet<>();
                while (generated.size() < 3) {
                    Integer n = RANDOMGEN.nextInt(4);
                    generated.add(n);
                }

                for (int number : generated) {
                    for (int i = 0; i < configs.getNContestants(); i++) {
                        if (i == number) {
                            wasPicked[teamID][i] = true;
                            waitingForPick[teamID][i].signal();
                            pickedContestants[teamID][j++] = i;
                        }
                    }
                }
            } else if (strategy.equals(CoachStrategy.Strategy.PICK_FIRST_THREE_CONTESTANTS.shortName())) {
                for (int i = 0; i < configs.getNContestants(); i++) {
                    if (i <= 2) {
                        wasPicked[teamID][i] = true;
                        waitingForPick[teamID][i].signal();
                        pickedContestants[teamID][j++] = i;
                    } else break;
                }
            } else { // last three
                for (int i = 0; i < configs.getNContestants(); i++) {
                    if (i >= 2) {
                        wasPicked[teamID][i] = true;
                        waitingForPick[teamID][i].signal();
                        pickedContestants[teamID][j++] = i;
                    }
                }

            }


            //coach.changeState(CoachState.ASSEMBLE_TEAM);
            CoachState state = CoachState.ASSEMBLE_TEAM;
            repository.updateCoachState(state.shortName(), teamID);

            return new Response(null, state.shortName());
        } finally {

            mutex.unlock();
        }

    }

    /**
     * Checks to see if a contestant was picked by his coach.
     * @return current trial number
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public Response waitForContestantCall(int gameMemberID, int teamID) throws InterruptedException {

        mutex.lock();

        try {

            while(!wasPicked[teamID][gameMemberID])
                waitingForPick[teamID][gameMemberID].await();

            if(!contestantsHaveMoreOperations) {
                return new Response(null, configs.getMaxTrials() + 1);
            } else
                wasPicked[teamID][gameMemberID] = false;
            //contestant.callContestant(false);



            return new Response(null, strengths[teamID][gameMemberID]);

        } finally {
            mutex.unlock();
        }
    }

    /**
     * Transition.
     */
    @Override
    public Response seatDown(int gameMemberID, int teamID, int strength, int position, boolean matchOver) throws RemoteException {
        mutex.lock();

        try {

            // contestant.removePlaygroundPosition();
            if (nContestants[teamID] < configs.getNContestants()) {
                nContestants[teamID]++;
                this.assignStrength(teamID, gameMemberID, strength);
            } else
                repository.removeContestantFromPosition(teamID, position);

            String state = ContestantState.SEAT_AT_THE_BENCH.shortName();
            repository.updateContestantState(state, gameMemberID, teamID);

            if ((matchOver)) {
                firstToEndMatch++;
                if (firstToEndMatch < 6) return new Response(null);

                this.contestantsHaveMoreOperations = false;

                for (int i = 0; i < configs.getNTeams(); i++) {
                    for (int j = 0; j < configs.getNContestants(); j++) {
                        // c.callContestant(true);

                        wasPicked[i][j] = true;
                        waitingForPick[i][j].signal();

                        // c.hasNoMoreOperations();
                    }
                }

            }

            return new Response(null, state);

        } finally {
            mutex.unlock();
        }

    }

    private void assignStrength(int teamID, int gameMemberID, int strength) throws RemoteException {
        strengths[teamID][gameMemberID] = strength;
        if(nContestants[teamID] == configs.getNContestants())
            repository.updateStrengths(teamID, strengths[teamID]);

    }

    @Override
    public Response contestantsHaveMoreOperations()
    {
        mutex.lock();
        try {
            return new Response(null, this.contestantsHaveMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    private int nrequestsToDie = 0;
    @Override
    public boolean closeBenchConnection() throws RemoteException {
        mutex.lock();

        try {
            nrequestsToDie++;

            if (nrequestsToDie == configs.getNTeams() * configs.getNContestants()) {
                repository.requestToDie();
                return true;
            }

            return false;
        } finally {
            mutex.unlock();
        }
    }

}
