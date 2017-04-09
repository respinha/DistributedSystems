package pt.ua.sd.ropegame.bench;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
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
public class Bench implements ICoachBench, IContestantsBench, IRefBench {

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
    private VectClock vectClock;

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

        vectClock = new VectClock(configs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response callTrial(VectClock clientClock) throws RemoteException {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            if (nCoachesBeingCalled == 3) nCoachesBeingCalled = 0;

            nCoachesBeingCalled++;
            waitingForTrialToStart.signalAll();
            repository.updateRefState(vectClock, RefereeState.TEAMS_READY.shortName());

            return new Response(vectClock, RefereeState.TEAMS_READY.shortName());
        } finally {
            mutex.unlock();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response waitForCoachCall(VectClock clientClock) throws InterruptedException, RemoteException {
        mutex.lock();
        try {
            vectClock.update(clientClock);
            nCoachesBeingCalled++;
            while (nCoachesBeingCalled < configs.getNCoaches() + 1)
                waitingForTrialToStart.await();
            waitingForTrialToStart.signalAll();

            return new Response(vectClock);
        } finally {
            mutex.unlock();
        }



    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response coachesHaveMoreOperations() throws RemoteException{
        mutex.lock();

        try {
            return new Response(coachesHaveMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response reviewNotes(VectClock clientClock, int teamID, int trial, boolean knockout) throws RemoteException {
        mutex.lock();

        try {
			// updating vector clock
		
            vectClock.update(clientClock);
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


                repository.updateStrengths(vectClock, teamID, strengths[teamID]);
            }


            CoachState state = CoachState.WAIT_FOR_REFEREE_COMMAND;
            repository.updateCoachState(vectClock, state.shortName(), teamID);

            return new Response(vectClock, state.shortName());
        } finally {
            mutex.unlock();
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response callContestants(VectClock clientClock, int teamID, String strategy) throws RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
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
            repository.updateCoachState(vectClock, state.shortName(), teamID);

            return new Response(vectClock, state.shortName());
        } finally {

            mutex.unlock();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response waitForContestantCall(VectClock clientClock, int gameMemberID, int teamID) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            repository.updateClock(vectClock);
            while(!wasPicked[teamID][gameMemberID])
                waitingForPick[teamID][gameMemberID].await();

            if(!contestantsHaveMoreOperations) {
                // todo: alternativamente, repository.updateContestantState(vectClock, ContestantState.SEAT_AT_THE_BENCH.shortName(), gameMemberID, teamID);
                return new Response(vectClock, configs.getMaxTrials() + 1, false);
            } else
                wasPicked[teamID][gameMemberID] = false;
            //contestant.callContestant(false);



            return new Response(vectClock, strengths[teamID][gameMemberID], true);

        } finally {
            mutex.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response seatDown(VectClock clientClock, int gameMemberID, int teamID, int strength, int position, boolean matchOver) throws RemoteException {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            // contestant.removePlaygroundPosition();
            if (nContestants[teamID] < configs.getNContestants()) {
                nContestants[teamID]++;
                this.assignStrength(teamID, gameMemberID, strength);
            } else
                repository.removeContestantFromPosition(vectClock, teamID, position);

            String state = ContestantState.SEAT_AT_THE_BENCH.shortName();
            repository.updateContestantState(vectClock, state, gameMemberID, teamID);

            if ((matchOver)) {
                firstToEndMatch++;
                if (firstToEndMatch < 6) return new Response(vectClock, state);

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

            return new Response(vectClock, state);

        } finally {
            mutex.unlock();
        }

    }

    /**
     * Assigns strength for a given contestant.
     * @param teamID contestant's team.
     * @param gameMemberID contestant's number.
     * @param strength contestant's strength
     * @throws RemoteException A remote exception occurred.
     */
    private void assignStrength(int teamID, int gameMemberID, int strength) throws RemoteException {
        strengths[teamID][gameMemberID] = strength;
        if(nContestants[teamID] == configs.getNContestants())
            repository.updateStrengths(vectClock, teamID, strengths[teamID]);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response contestantsHaveMoreOperations() throws RemoteException
    {
        mutex.lock();
        try {
            return new Response(this.contestantsHaveMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    private int nrequestsToDie = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeBenchConnection() throws RemoteException {
        mutex.lock();

        try {
            nrequestsToDie++;

            if (nrequestsToDie == configs.getNTeams() * configs.getNContestants()) {
                try {repository.closeConnection(); }
                catch (RemoteException e) {}
                System.out.println("O banco foi desligado.");
                System.exit(0);
            }

        } finally {
            mutex.unlock();
        }
    }

}
