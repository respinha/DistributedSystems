package pt.ua.sd.ropegame.refereesite;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.Response;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor that allows for communication between the referee and the coach.
 */
class RefereeSite implements IRefRefSite, ICoachRefSite {

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
    public Response startTheMatch() throws RemoteException {

        mutex.lock();

        try {
            String state = RefereeState.START_OF_THE_MATCH.shortName();
            repository.updateRefState(state);
            return new Response(null, state);
        } finally {
            mutex.unlock();
        }

    }

    @Override
    public Response refHasMoreOperations() throws RemoteException {

        mutex.lock();
        try {
            return new Response(null, this.refHasMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void closeRefSite() throws RemoteException {
        mutex.lock();

        repository.requestToDie();

        mutex.unlock();
    }

    /**
     * Transition.
     */
    @Override
    public Response announceNewGameRefSite() throws RemoteException {

        mutex.lock();

        try {
            RefereeState state = RefereeState.START_OF_A_GAME;
            // referee.changeState(state);
            currentGame++;
            repository.updateRefState(state.shortName());
            repository.updateGame(currentGame);

            return new Response(null, state.shortName());
        } finally {

            mutex.unlock();
        }
    }

    /**
     * Block the referee until the last coach informs her all contestants are ready.
     * @throws InterruptedException Thread was interrupted.
     */
    @Override
    public Response startTrialRefSite() throws InterruptedException, RemoteException{

        mutex.lock();

        try {
            while (!coachAndContestantsReady)
                waitingForCoachAndPlayers.await();


            coachAndContestantsReady = false;
            return new Response(null);
        } finally {

            mutex.unlock();
        }
    }

    /**
     *
     * @param currentTrial The trial in  which the referee is asserting her decision.
     * @param knockout Boolean that checks if there has been a knockout in this trial.
     * @return
     */
    @Override
    public Response assertTrialDecisionRefSite(int currentTrial, boolean knockout) throws RemoteException {

        mutex.lock();

        try {

            if (currentTrial == configs.getMaxTrials() || knockout) {
                RefereeState state = RefereeState.END_OF_A_GAME;
                // referee.changeState(state);
                repository.updateRefState(state.shortName());

                return new Response(null, state.shortName(), false);
            }


            return new Response(null, true);
        } finally {
            mutex.unlock();
        }

    }


    /**
     * Called by the coach of the last team to arrive at the playground to inform the referee the trial can start.
     * @param coachTeamID
     */
    @Override
    public Response informReferee(int coachTeamID) throws RemoteException {

        mutex.lock();

        try {
            coachAndContestantsReady = true;
            waitingForCoachAndPlayers.signal();

            CoachState state = CoachState.WATCH_TRIAL;
            // coach.changeState(state);

            repository.updateCoachState(state.shortName(), coachTeamID);
            return new Response(null, state.shortName());
        } finally {

            mutex.unlock();
        }

    }


    /**
     * Update general repository with last game stats.
     * @param ntrials number of trials to win.
     * @param ropePos
     * @param knockout true if the victory was achieved by knockout.
     */
    @Override
    public Response declareGameWinner(int ntrials, int ropePos, boolean knockout) throws RemoteException {

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
            // referee.(state);
            repository.updateRefState(state.shortName());

            return new Response(null, state.shortName(), endOfMatch);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update repository with match stats.
     */
    @Override
    public Response declareMatchWinner() throws RemoteException {

        mutex.lock();

        try {
            int matchWinner = (teamGameScores[0] != teamGameScores[1]) ? ((teamGameScores[0] > teamGameScores[1]) ? 1 : 2) : 0;

            // referee.hasNoMoreOperations();
            refHasMoreOperations = false;
            //String state = RefereeState.END_OF_THE_MATCH.shortName();
            //repository.updateRefState(state);

            repository.updateMatchWinner(matchWinner, teamGameScores);
            repository.generateLogFile();

            return new Response(null);
        } finally {
            mutex.unlock();
        }

    }

}
