package pt.ua.sd.ropegame.refereesite;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
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

    private VectClock vectClock;
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

        vectClock = new VectClock(configs);
    }

    /**
     * Method used make the first refereee state update.
     */
    @Override
    public Response startTheMatch(VectClock clientClock) throws RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            String state = RefereeState.START_OF_THE_MATCH.shortName();
            repository.updateRefState(vectClock, state);
            return new Response(vectClock, state);
        } finally {
            mutex.unlock();
        }

    }

    @Override
    public Response refHasMoreOperations() throws RemoteException {

        mutex.lock();
        try {
            return new Response(this.refHasMoreOperations);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void closeRefSite() throws RemoteException {
        mutex.lock();

        System.out.println(vectClock);

        repository.requestToDie();

        mutex.unlock();
    }

    /**
     * Transition.
     */
    @Override
    public Response announceNewGameRefSite(VectClock clientClock) throws RemoteException {

        mutex.lock();

        try {
            RefereeState state = RefereeState.START_OF_A_GAME;
            // referee.changeState(state);
            vectClock.update(clientClock);
            currentGame++;
            repository.updateRefState(vectClock, state.shortName());
            repository.updateGame(vectClock, currentGame);

            return new Response(vectClock, state.shortName());
        } finally {

            mutex.unlock();
        }
    }

    /**
     * Block the referee until the last coach informs her all contestants are ready.
     * @throws InterruptedException Thread was interrupted.
     */
    @Override
    public Response startTrialRefSite(VectClock clientClock) throws InterruptedException, RemoteException{

        mutex.lock();

        try {
            vectClock.update(clientClock);
            while (!coachAndContestantsReady)
                waitingForCoachAndPlayers.await();

            coachAndContestantsReady = false;
            return new Response(vectClock);
        } finally {

            mutex.unlock();
        }
    }

    /**
     * Called by the coach of the last team to arrive at the playground to inform the referee the trial can start.
     * @param coachTeamID
     */
    @Override
    public Response informReferee(VectClock clientClock, int coachTeamID) throws RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            coachAndContestantsReady = true;
            waitingForCoachAndPlayers.signal();

            CoachState state = CoachState.WATCH_TRIAL;
            // coach.changeState(state);

            repository.updateCoachState(vectClock, state.shortName(), coachTeamID);
            return new Response(vectClock, state.shortName());
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
    public Response declareGameWinner(VectClock clientClock, int ntrials, int ropePos, boolean knockout) throws RemoteException {

        mutex.lock();

        try {
            // declare game winner
            vectClock.update(clientClock);
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

            repository.updateGameWinner(vectClock, currentGame, gameWinner, ntrials, knockout);
            // referee.(state);
            repository.updateRefState(vectClock, state.shortName());

            return new Response(vectClock, state.shortName(), endOfMatch);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update repository with match stats.
     */
    @Override
    public Response declareMatchWinner(VectClock clientClock) throws RemoteException {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            int matchWinner = (teamGameScores[0] != teamGameScores[1]) ? ((teamGameScores[0] > teamGameScores[1]) ? 1 : 2) : 0;

            // referee.hasNoMoreOperations();
            refHasMoreOperations = false;
            //String state = RefereeState.END_OF_THE_MATCH.shortName();
            //repository.updateRefState(state);

            repository.updateMatchWinner(vectClock, matchWinner, teamGameScores);

            return new Response(vectClock);
        } finally {
            mutex.unlock();
        }
    }

}
