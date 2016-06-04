package pt.ua.sd.ropegame.playground;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
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
    private VectClock vectClock;


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
        vectClock = new VectClock(configs);
    }

    /**
     * Move a contestant to the playground.
     * @throws InterruptedException When thread is interrupted
     */
    @Override
     public Response standInLine(VectClock clientClock, int gameMemberID, int teamID, int strength) throws RemoteException {

        mutex.lock();
        try {
            vectClock.update(clientClock);
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
            repository.updateContestantState(vectClock, ContestantState.STAND_IN_POSITION.shortName(), gameMemberID, teamID);
            repository.updateContestantPosition(vectClock, gameMemberID, teamID, pos);
            return new Response(vectClock, ContestantState.STAND_IN_POSITION.shortName(), pos);
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
    public Response moveCoachToPlayground(VectClock clientClock, int teamID) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            while (nContInPlayground < configs.getMaxContsPlayground())
                coachWaitingForContestants.await();


            nCoachesInPlayground++;

            // if all coaches and contestants are in the playground, we can inform referee.
            /* if coach doesn't inform referee, its state can be updated now. else, it is updated on @RefereeSite */

            int coachToInform = (nCoachesInPlayground == configs.getNCoaches()) ? teamID : -1;
            String state = "";

            if (nCoachesInPlayground == 1) {
                state = CoachState.WATCH_TRIAL.shortName();
                // teamID.changeState(state);
                repository.updateCoachState(vectClock,state, teamID);
            }
            if (nCoachesInPlayground == configs.getNCoaches()) {
                nCoachesInPlayground = 0;
                nContInPlayground = 0;
            }

            return new Response(vectClock, state, coachToInform);
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
    public Response getReady(VectClock clientClock, int gameMemberID, int teamID, int strength) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            while (!trialStarted)
                waitingForTrialToStart.await();

            nContestantsReady++;
            if (nContestantsReady == configs.getMaxContsPlayground()) {
                trialStarted = false;
                nContestantsReady = 0;
            }

            ContestantState st = ContestantState.DO_YOUR_BEST;
            // contestant.changeState(st);
            repository.updateContestantState(vectClock, st.shortName(), gameMemberID, teamID);

            return new Response(vectClock, st.shortName());
        } finally {
            mutex.unlock();
        }

    }

    /**
     * Called by referee. Wakes up all enteties waiting for the trial to start.
     * @return current trial.
     */
    @Override
    public Response startTrialPlayground(VectClock clientClock) throws RemoteException {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            trialStarted = true;
            waitingForTrialToStart.signalAll();


            // reset current trial
            if(currentTrial == configs.getMaxTrials() || knockout)
                this.currentTrial = 0;

            currentTrial++;

            repository.updateTrial(vectClock, currentTrial);

            RefereeState state = RefereeState.WAIT_FOR_TRIAL_CONCLUSION;
            // referee.changeState(state);
            repository.updateRefState(vectClock, state.shortName());

            return new Response(vectClock, state.shortName(), currentTrial);

        } finally {

            mutex.unlock();
        }

    }


    /**
     * Simulate that a rope is being pulled. Wait for a random interval.
     * @throws InterruptedException The thread was interrupted.
     */
    @Override
    public Response pullTheRope(VectClock clientClock) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            int r = RANDOMGEN.nextInt(100) + 1;
            Thread.sleep(r);

            return new Response(vectClock);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Called every time a contestant finishes pulling the rope.<p>
     * The last contestant to perform this operation wakes up the referee.
     * @return true if game ended due to knockout.
     * @throws InterruptedException When thread is interrupted.
     */
    @Override
    public Response amDone(VectClock clientClock) throws InterruptedException, RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            contestantsDone++;

            // if this is the last contestant
            if (contestantsDone == configs.getMaxContsPlayground()) {
                issuingTrial.signal();
            }

            // wait for trial decision
            while (!decided)
                waitingForTrialDecision.await();

            return new Response(vectClock, (gameOver && game == configs.getMaxGames()));
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
    public Response assertTrialDecision(VectClock clientClock) throws InterruptedException, RemoteException {
        mutex.lock();

       try {
           vectClock.update(clientClock);
           while (contestantsDone < configs.getMaxContsPlayground())
               issuingTrial.await();

           System.out.println("passei aqui");

           if(teamStrength[1] > teamStrength[0])
               ropePos++;
           else if(teamStrength[1] < teamStrength[0])
               ropePos--;
           repository.updateRopePosition(vectClock, ropePos);

           knockout = (Math.abs(ropePos) >= 4);
           teamStrength[1] = teamStrength[0] = 0;

           String refState;

           if(currentTrial == configs.getMaxTrials() || knockout) {
               refState = RefereeState.END_OF_A_GAME.shortName();
               gameOver = true;
           }
           else refState = RefereeState.TEAMS_READY.shortName();

           repository.updateRefState(vectClock, refState);

           decided = true;
           waitingForTrialDecision.signalAll();
           if(contestantsDone == 6) contestantsDone = 0;


           return new Response(vectClock, refState, ropePos, knockout, gameOver);
       } finally {

           mutex.unlock();
       }

    }

    /**
     * Called by referee to reset rope position.
     */
    @Override
    public Response announceNewGamePlayground(VectClock clientClock) throws RemoteException {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            this.ropePos = 0;
            gameOver = false;
            game++;
            repository.updateRopePosition(vectClock, ropePos);

            return new Response(vectClock);
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
    public Response reviewNotes(VectClock clientClock, int teamID) throws InterruptedException, RemoteException {
        mutex.lock();

        try {
            vectClock.update(clientClock);
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

            return new Response(vectClock, strategy, currentTrial, knockout);
        } finally {
            mutex.unlock();
        }


    }

    private int ncloseRequests = 0;

    @Override
    public void closePlaygroundConnection() throws RemoteException {
        mutex.lock();

        try {
            ncloseRequests++;

            if (ncloseRequests == configs.getNCoaches())
                repository.requestToDie();

        } finally {
            mutex.unlock();
        }
    }
}
