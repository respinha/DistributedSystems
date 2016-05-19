package pt.ua.sd.ropegame.memregions;

import pt.ua.sd.ropegame.entities.Coach;
import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.enums.CoachState;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.enums.RefereeState;
import pt.ua.sd.ropegame.interfaces.*;
import pt.ua.sd.ropegame.utils.GameOfTheRopeConfigs;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Memory region where 3 players of each team will try to win the Game of the Rope.
 */
public class Playground implements ICoachPlay, IContestantsPlay, IRefPlay {


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


    /**
     * Constructor for Playground.
     * @param repo general repository.
     */
    public Playground(IPlaygroundGenRep repo) {

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

    }

    /**
     * Move a contestant to the playground.
     * @throws InterruptedException When thread is interrupted
     */
    @Override
    public int standInLine(Contestant contestant, int gameMemberID, int teamID, int strength) throws InterruptedException {

        mutex.lock();

        try {
            nContInPlayground++;

            teamStrength[teamID] += strength;


            int pos = teamID == 0 ? ++playgroundPosTeam1 : ++playgroundPosTeam2;
            // contestant.assignPosition(pos);

            // if all players arrived to playground, coaches may be awaken
            if (nContInPlayground == GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND) {
                coachWaitingForContestants.signalAll();
                playgroundPosTeam1 = 0;
                playgroundPosTeam2 = 0;
                decided = false;
            }

            contestant.changeState(ContestantState.STAND_IN_POSITION);
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
    public int moveCoachToPlayground(Coach coach, int teamID) throws InterruptedException {

        mutex.lock();

        try {
            while (nContInPlayground < GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND)
                coachWaitingForContestants.await();


            nCoachesInPlayground++;

            // if all coaches and contestants are in the playground, we can inform referee.
        /* if coach doesn't inform referee, its state can be updated now. else, it is updated on @RefereeSite */

            int coachToInform = (nCoachesInPlayground == GameOfTheRopeConfigs.N_COACHES) ? teamID : -1;

            if (nCoachesInPlayground == 1) {
                CoachState state = CoachState.WATCH_TRIAL;
                coach.changeState(state);
                repository.updateCoachState(state.shortName(), teamID);
            }
            if (nCoachesInPlayground == GameOfTheRopeConfigs.N_COACHES) {
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
    public void getReady(Contestant contestant, int gameMemberID, int teamID, int strength) throws InterruptedException {

        mutex.lock();


        while (!trialStarted)
            waitingForTrialToStart.await();

        nContestantsReady++;
        if(nContestantsReady == GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND) {
            trialStarted = false;
            nContestantsReady = 0;
        }

        ContestantState st = ContestantState.DO_YOUR_BEST;
        contestant.changeState(st);
        repository.updateContestantState(st.shortName(), gameMemberID, teamID);
        mutex.unlock();

    }

    /**
     * Called by referee. Wakes up all enteties waiting for the trial to start.
     * @return current trial.
     */
    @Override
    public int startTrial(Referee referee) {
        mutex.lock();

        try {

            trialStarted = true;
            waitingForTrialToStart.signalAll();



            // reset current trial
            if(currentTrial == GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND || knockout)
                this.currentTrial = 0;

            currentTrial++;

            repository.updateTrial(currentTrial);

            RefereeState state = RefereeState.WAIT_FOR_TRIAL_CONCLUSION;
            referee.changeState(state);
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
            if (contestantsDone == GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND) {
                issuingTrial.signal();
            }

            // wait for trial decision
            while (!decided)
                waitingForTrialDecision.await();

            return knockout;
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
    public boolean assertTrialDecision() throws InterruptedException {
        mutex.lock();

        try {
            while (contestantsDone < GameOfTheRopeConfigs.MAX_CONTESTANTS_IN_PLAYGROUND)
                issuingTrial.await();


            if(teamStrength[1] > teamStrength[0])
                ropePos++;
            else if(teamStrength[1] < teamStrength[0])
                ropePos--;
            repository.updateRopePosition(ropePos);

            knockout = (Math.abs(ropePos) >= 4);
            teamStrength[1] = teamStrength[0] = 0;

            if(currentTrial == GameOfTheRopeConfigs.MAX_TRIALS || knockout) repository.updateRefState(RefereeState.END_OF_A_GAME.shortName());
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
    public void announceNewGame() {
        mutex.lock();

        this.ropePos = 0;
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
    public boolean isKnockout()
    {
        mutex.lock();
        try {

            return knockout;
        } finally {
            mutex.unlock();
        }
    }
    }
