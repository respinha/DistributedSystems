package pt.ua.sd.ropegame.refereesite;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor that allows for communication between the referee and the coach.
 */
public class RefereeSite implements IRefSite, ICoachRefSite {

    private int currentGame;
    private Lock mutex;

    private Condition waitingForCoachAndPlayers;
    private boolean coachAndContestantsReady;

    // game stats
    //private int[] teamTrialScores;
    private int[] teamGameScores;
    private boolean refHasMoreOperations;

    private IRefSiteGenRep repository;

    private GameOfTheRopeConfigs configs;

    /**
     * Constructor
     * @param repo General Repository.
     */
    public RefereeSite(IRefSiteGenRep repo, GameOfTheRopeConfigs configs) {

        this.configs = configs;

        this.repository = repo;
        mutex = new ReentrantLock();

        waitingForCoachAndPlayers = mutex.newCondition();

        coachAndContestantsReady = false;

        //teamTrialScores = new int[2];
        teamGameScores = new int[2];
        //teamTrialScores[0] = teamTrialScores[1] = 0;
        teamGameScores[0] = teamGameScores[1] = 0;

        currentGame = 0;

        refHasMoreOperations = true;
    }

    /**
     * Method used make the first refereee state update.
     */
    @Override
    public void startTheMatch() {

        mutex.lock();

        repository.updateRefState(RefereeState.START_OF_THE_MATCH.shortName());

        mutex.unlock();
    }

    @Override
    public boolean refHasMoreOperations() {

        mutex.lock();
        try {
            return this.refHasMoreOperations;
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void closeRefSite() {
        mutex.lock();

        repository.requestToDie();

        mutex.unlock();
    }

    /**
     * Transition.
     */
    @Override
    public void announceNewGameRefSite() {

        mutex.lock();


        RefereeState state = RefereeState.START_OF_A_GAME;
        // referee.changeState(state);
        currentGame++;
        repository.updateRefState(state.shortName());
        repository.updateGame(currentGame);

        mutex.unlock();
    }

    /**
     * Block the referee until the last coach informs her all contestants are ready.
     * @throws InterruptedException Thread was interrupted.
     */
    @Override
    public void startTrialRefSite() throws InterruptedException {

        mutex.lock();

        while(!coachAndContestantsReady)
            waitingForCoachAndPlayers.await();

        coachAndContestantsReady = false;

        mutex.unlock();
    }

    /**
     *
     * @param currentTrial The trial in  which the referee is asserting her decision.
     * @param knockout Boolean that checks if there has been a knockout in this trial.
     * @return
     */
    @Override
    public boolean assertTrialDecisionRefSite(int currentTrial, boolean knockout) {

        mutex.lock();

        try {

            if (currentTrial == configs.getMaxTrials() || knockout) {
                RefereeState state = RefereeState.END_OF_A_GAME;
                // referee.changeState(state);
                repository.updateRefState(state.shortName());

                return false;
            }


            return true;
        } finally {
            mutex.unlock();
        }

    }


    /**
     * Called by the coach of the last team to arrive at the playground to inform the referee the trial can start.
     * @param coachTeamID
     */
    @Override
    public void informReferee(int coachTeamID) {

        mutex.lock();

        coachAndContestantsReady = true;
        waitingForCoachAndPlayers.signal();

        CoachState state = CoachState.WATCH_TRIAL;
        // coach.changeState(state);
        repository.updateCoachState(state.shortName(), coachTeamID);

        mutex.unlock();
    }


    /**
     * Update general repository with last game stats.
     * @param ntrials number of trials to win.
     * @param ropePos
     * @param knockout true if the victory was achieved by knockout.
     */
    @Override
    public boolean declareGameWinner(int ntrials, int ropePos, boolean knockout) {

        mutex.lock();

        try {
            // declare game winner

            int gameWinner = ropePos != 0 ? (ropePos > 0 ? 1:2) : 0;

            switch (gameWinner) {
                case 0:
                    break;
                case 1:
                    teamGameScores[0]++;
                    break;
                case 2:
                    teamGameScores[1]++;
                    break;
            }

            boolean endOfMatch;
            // check if match has ended
            RefereeState state;
            if (currentGame == configs.getMaxGames()) {
                state = RefereeState.END_OF_THE_MATCH;
                endOfMatch = true;
            } else {
                state = RefereeState.START_OF_A_GAME;
                endOfMatch = false;
            }

            repository.updateGameWinner(currentGame, gameWinner, ntrials, knockout);
            // referee.changeState(state);
            repository.updateRefState(state.shortName());

            return endOfMatch;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update repository with match stats.
     */
    @Override
    public void declareMatchWinner() {

        mutex.lock();

        int matchWinner = (teamGameScores[0] != teamGameScores[1]) ? ((teamGameScores[0] > teamGameScores[1]) ? 1 : 2) : 0;
        // referee.hasNoMoreOperations();
        refHasMoreOperations = false;
        repository.updateRefState(RefereeState.END_OF_THE_MATCH.shortName());

        repository.updateMatchWinner(matchWinner, teamGameScores);
        repository.generateLogFile();

        mutex.unlock();

    }

}
