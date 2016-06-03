package pt.ua.sd.ropegame.playground;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Memory region where 3 players of each team will try to win the Game of the Rope.
 */
class Playground implements ICoachPlay, IContestantsPlay, IRefPlay {

    private int currentTrial;

    private Condition waitingForTrialToStart;
    private Lock mutex;
    private Condition coachWaitingForContestants;

    private int nCoachesInPlayground;
    private int nContInPlayground;

    private boolean trialStarted;
    private boolean decided;

    private int playgroundPosTeam1, playgroundPosTeam2;
    private int ropePos;
    private boolean knockout;

    private int contestantsDone;
    private Condition issuingTrial;
    private Condition waitingForTrialDecision;

    private int nContestantsReady;

    private int teamStrength[];

    private IPlaygroundGenRep repository;

    private static final Random RANDOMGEN = new Random();
    private boolean gameOver;
    private int game;

    private GameOfTheRopeConfigs configs;


    /**
     * Constructor for Playground.
     * @param repo general repository.
     */
    public Playground(IPlaygroundGenRep repo, GameOfTheRopeConfigs configs) {

        this.configs = configs;

        mutex = new ReentrantLock();
        coachWaitingForContestants = mutex.newCondition();
        waitingForTrialToStart = mutex.newCondition();
        issuingTrial = mutex.newCondition();
        waitingForTrialDecision = mutex.newCondition();

        nCoachesInPlayground = 0;
        nContInPlayground = 0;

        playgroundPosTeam1 = playgroundPosTeam2 = ropePos = 0;

        trialStarted = false;
        decided = false;
        contestantsDone = 0;
        currentTrial = 0;

        nContestantsReady = 0;

        repository = repo;

        teamStrength = new int[2];

        game = 0;
    }

    /**
     * Move a contestant to the playground.
     * @throws InterruptedException When thread is interrupted
     */
    @Override
     public int standInLine(int gameMemberID, int teamID, int strength) throws RemoteException {

         mutex.lock();
        try {


            nContInPlayground++;

            teamStrength[teamID] += strength;


            int pos = teamID == 0 ? ++playgroundPosTeam1 : ++playgroundPosTeam2;
            // contestant.assignPosition(pos);

            // if all players arrived to playground, coaches may be awaken
            if (nContInPlayground == configs.getMaxContsPlayground()) {
                coachWaitingForContestants.signalAll();
                playgroundPosTeam1 = 0;
                playgroundPosTeam2 = 0;
                decided = false;
            }

            // contestant.changeState(ContestantState.STAND_IN_POSITION);
            repository.updateContestantState(ContestantState.STAND_IN_POSITION.shortName(), gameMemberID, teamID);
            repository.updateContestantPosition(gameMemberID, teamID, pos);
            return pos;
        } finally {
            mutex.unlock();
        }

    }

    /**
     * Move coach to the playground and wait for all the contestants to arrive.
     * @param teamID A Coach.
     * @throws InterruptedException If Thread was interrupted.
     */
    @Override
    public int moveCoachToPlayground(int teamID) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            while (nContInPlayground < configs.getMaxContsPlayground())
                coachWaitingForContestants.await();


            nCoachesInPlayground++;

            // if all coaches and contestants are in the playground, we can inform referee.
        /* if coach doesn't inform referee, its state can be updated now. else, it is updated on @RefereeSite */

            int coachToInform = (nCoachesInPlayground == configs.getNCoaches()) ? teamID : -1;

            if (nCoachesInPlayground == 1) {
                CoachState state = CoachState.WATCH_TRIAL;
                // teamID.changeState(state);
                repository.updateCoachState(state.shortName(), teamID);
            }
            if (nCoachesInPlayground == configs.getNCoaches()) {
                nCoachesInPlayground = 0;
                nContInPlayground = 0;
            }

            return coachToInform;
        } finally {

            mutex.unlock();
        }

    }

    /**
     * Wait for referee to start trial.
     *
     * @param gameMemberID
     * @param teamID
     * @throws InterruptedException If Thread was interrupted.
     */
    @Override
    public void getReady(int gameMemberID, int teamID, int strength) throws InterruptedException, RemoteException {

        mutex.lock();


        while (!trialStarted)
            waitingForTrialToStart.await();

        nContestantsReady++;
        if(nContestantsReady == configs.getMaxContsPlayground()) {
            trialStarted = false;
            nContestantsReady = 0;
        }

        ContestantState st = ContestantState.DO_YOUR_BEST;
        // contestant.changeState(st);
        repository.updateContestantState(st.shortName(), gameMemberID, teamID);
        mutex.unlock();

    }

    /**
     * Called by referee. Wakes up all enteties waiting for the trial to start.
     * @return current trial.
     */
    @Override
    public int startTrialPlayground() throws RemoteException {
        mutex.lock();

        try {

            trialStarted = true;
            waitingForTrialToStart.signalAll();


            // reset current trial
            if(currentTrial == configs.getMaxTrials() || knockout)
                this.currentTrial = 0;

            currentTrial++;

            repository.updateTrial(currentTrial);

            RefereeState state = RefereeState.WAIT_FOR_TRIAL_CONCLUSION;
            // referee.changeState(state);
            repository.updateRefState(state.shortName());

            return currentTrial;

        } finally {

            mutex.unlock();
        }

    }


    /**
     * Simulate that a rope is being pulled. Wait for a random interval.
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public void pullTheRope() throws InterruptedException {

        mutex.lock();
        int r = RANDOMGEN.nextInt(100)+1;
        // TODO: rever
        Thread.sleep(r);

        mutex.unlock();
    }

    /**
     * Called every time a contestant finishes pulling the rope.<p>
     * The last contestant to perform this operation wakes up the referee.
     * @return true if game ended due to knockout.
     * @throws InterruptedException When thread is interrupted.
     */
    @Override
    public boolean amDone() throws InterruptedException {

        mutex.lock();

        try {
            contestantsDone++;

            // c.hasPlayedInLastTrial();

            // if this is the last contestant
            if (contestantsDone == configs.getMaxContsPlayground()) {
                issuingTrial.signal();
            }

            // wait for trial decision
            while (!decided)
                waitingForTrialDecision.await();

            return (gameOver && game == configs.getMaxGames());
        } finally {

            mutex.unlock();
        }
    }

    /**
     * Referee waits for all contestants to finish pulling the rope and updates general repository with rope position.
     * @return true if game ended due to knockout.
     * @throws InterruptedException When thread is interrupted.
     */
    @Override
    public boolean assertTrialDecisionPlayground() throws InterruptedException, RemoteException {
        mutex.lock();

       try {
           while (contestantsDone < configs.getMaxContsPlayground())
               issuingTrial.await();


           if(teamStrength[1] > teamStrength[0])
               ropePos++;
           else if(teamStrength[1] < teamStrength[0])
               ropePos--;
           repository.updateRopePosition(ropePos);

           knockout = (Math.abs(ropePos) >= 4);
           teamStrength[1] = teamStrength[0] = 0;

           if(currentTrial == configs.getMaxTrials() || knockout) {
               repository.updateRefState(RefereeState.END_OF_A_GAME.shortName());
               gameOver = true;
           }
           else repository.updateRefState(RefereeState.TEAMS_READY.shortName());

           decided = true;
           waitingForTrialDecision.signalAll();
           if(contestantsDone == 6) contestantsDone = 0;


           return knockout;
       } finally {

           mutex.unlock();
       }

    }

    /**
     * Called by referee to reset rope position.
     */
    @Override
    public void announceNewGamePlayground() throws RemoteException {
        mutex.lock();

        this.ropePos = 0;
        gameOver = false;
        game++;
        repository.updateRopePosition(ropePos);

        mutex.unlock();
    }

    /**
     * @return current trial.
     */
    @Override
    public int getCurrentTrial() {
        mutex.lock();
        try {
            return currentTrial;
        } finally {
            mutex.unlock();
        }
    }

    /**
     *
     * @return rope position.
     */
    @Override
    public int getRopePos() {
        mutex.lock();
        try {
            return ropePos;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Called by coach in the end of a trial to change strategy based on what happened during the previous trial.
     * @return true if game ended due to knockout.
     * @throws InterruptedException The Thread was interrupted.
     * @param teamID
     */
    @Override
    public int reviewNotes(int teamID) throws InterruptedException {
        mutex.lock();

        try {
            while (!decided)
                waitingForTrialDecision.await();

            int strategy = -2;
            if(teamID == 0) {
                if(ropePos < 0)
                    strategy = 1;
                else if(ropePos == 0)
                    strategy = 0;
                else if (ropePos > 0)
                    strategy = -1;
            }

            else if(teamID == 1) {
                if(ropePos < 0)
                    strategy = -1;
                else if(ropePos == 0)
                    strategy = 0;
                else if (ropePos > 0)
                    strategy = 1;
            }

            return strategy;
        } finally {
            mutex.unlock();
        }


    }

    @Override
    public boolean isKnockout() {
        mutex.lock();
        try {
            return knockout;
        } finally {
            mutex.unlock();
        }

    }

    private int nrequestsToDie = 0;

    @Override
    public boolean closePlaygroundConnection() throws RemoteException {
        mutex.lock();

        try {
            nrequestsToDie++;

            if (nrequestsToDie == configs.getNCoaches()) {
                repository.requestToDie();
                return true;
            }

            return false;
        } finally {
            mutex.unlock();
        }
    }
}
