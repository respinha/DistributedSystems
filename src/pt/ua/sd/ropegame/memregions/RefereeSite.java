package pt.ua.sd.ropegame.memregions;

import genclass.GenericIO;
import pt.ua.sd.ropegame.entities.Coach;
import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.enums.CoachState;
import pt.ua.sd.ropegame.enums.RefereeState;
import pt.ua.sd.ropegame.interfaces.*;
import pt.ua.sd.ropegame.utils.GameOfTheRopeConfigs;

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
    private int[] teamTrialScores;
    private int[] teamGameScores;

    private boolean hasMoreOperations;

    private IRefSiteGenRep repository;

    /**
     * Constructor
     * @param repo General Repository.
     */
    public RefereeSite(IRefSiteGenRep repo) {

        repository = repo;
        mutex = new ReentrantLock();

        waitingForCoachAndPlayers = mutex.newCondition();
        // repository.setRefSite(this);
        coachAndContestantsReady = false;

        teamTrialScores = new int[2];
        teamGameScores = new int[2];
        teamTrialScores[0] = teamTrialScores[1] = 0;
        teamGameScores[0] = teamGameScores[1] = 0;

        currentGame = 0;

        hasMoreOperations = true;
    }

    /**
     * Method used make the first refereee state update.
     * @param referee The referee.
     */
    @Override
    public void startTheMatch(Referee referee) {

        mutex.lock();

        referee.changeState(RefereeState.START_OF_THE_MATCH);
        repository.updateRefState(RefereeState.START_OF_THE_MATCH.shortName());

        mutex.unlock();
    }

    @Override
    public boolean refHasMoreOperations() {
        return hasMoreOperations;
    }

    /**
     * Transition
     * @param referee The referee.
     */
    @Override
    public void announceNewGame(Referee referee) {

        mutex.lock();


        RefereeState state = RefereeState.START_OF_A_GAME;
        referee.changeState(state);
        repository.updateRefState(state.shortName());
        currentGame++;

        mutex.unlock();
    }

    /**
     * Block the referee until the last coach informs her all contestants are ready.
     * @throws InterruptedException Thread was interrupted.
     */
    @Override
    public void startTrial() throws InterruptedException {

        mutex.lock();

        while(!coachAndContestantsReady)
            waitingForCoachAndPlayers.await();

        coachAndContestantsReady = false;

        mutex.unlock();
    }

    @Override
    public boolean assertTrialDecision(Referee referee, int currentTrial, int ropePos, boolean knockout) {

        mutex.lock();

        try {
            if (ropePos > 0)
                teamTrialScores[1]++;
            else if (ropePos < 0)
                teamTrialScores[0]++;


            if (currentTrial == GameOfTheRopeConfigs.MAX_TRIALS || knockout) {
                RefereeState state = RefereeState.END_OF_A_GAME;
                referee.changeState(state);
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
     * @param teamID
     */
    @Override
    public void informReferee(Coach coach, int teamID) {

        mutex.lock();

        coachAndContestantsReady = true;
        waitingForCoachAndPlayers.signal();

        CoachState state = CoachState.WATCH_TRIAL;
        coach.changeState(state);
        repository.updateCoachState(state.shortName(), teamID);

        mutex.unlock();
    }


    /**
     * Update general repository with last game stats.
     * @param referee The referee.
     * @param ntrials number of trials to win.
     * @param knockout true if the victory was achieved by knockout.
     */
    @Override
    public boolean declareGameWinner(Referee referee, int ntrials, boolean knockout) {

        mutex.lock();

        try {
            // declare game winner

            int gameWinner = (teamTrialScores[0] != teamTrialScores[1]) ? ((teamTrialScores[0] > teamTrialScores[1]) ? 1 : 2) : 0;

            teamTrialScores[0] = teamTrialScores[1] = 0;

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
            if (currentGame == GameOfTheRopeConfigs.MAX_GAMES) {
                state = RefereeState.END_OF_THE_MATCH;
                endOfMatch = true;
            } else {
                state = RefereeState.START_OF_A_GAME;
                endOfMatch = false;
            }

            repository.updateGameWinner(currentGame, gameWinner, ntrials, knockout);
            referee.changeState(state);
            repository.updateRefState(state.shortName());

            return endOfMatch;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update repository with match stats.
     * @param referee The referee.
     */
    @Override
    public void declareMatchWinner(Referee referee) {

        mutex.lock();

        int matchWinner = (teamGameScores[0] != teamGameScores[1]) ? ((teamGameScores[0] > teamGameScores[1]) ? 1 : 2) : 0;
        referee.hasNoMoreOperations();
        repository.updateRefState(RefereeState.END_OF_THE_MATCH.shortName());

        hasMoreOperations = false;
        repository.updateMatchWinner(matchWinner, teamGameScores);
        repository.generateLogFile();

        mutex.unlock();

    }

}
