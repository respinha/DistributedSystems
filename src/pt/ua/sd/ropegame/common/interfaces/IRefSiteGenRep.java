package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import java.rmi.RemoteException;

/**
 * An interface every General Repository-like object must implement to allow a Referee site to connect to a genrep.
 */
public interface IRefSiteGenRep extends IGeneralRepository {

    /**
     * Update current game.
     * @param clientClock Referee site's current clock.
     * @param game Current game.
     * @throws RemoteException A remote exception occurred.
     */
    void updateGame(VectClock clientClock, int game) throws RemoteException;

    /**
     * Displays "Match was won by team # (#-#). / was a draw." message.
     * @param clientClock Referee's current clock.
     * @param winner Team which won the match.
     * @param results Current match results.
     * @throws RemoteException A remote exception occurred.
     */
    void updateMatchWinner(VectClock clientClock, int winner, int[] results) throws RemoteException;

    /**
     * Displays "Game # was won by team # by knock out in # trials. / by points. / was a draw." message.
     * @param clientClock Referee's current clock.
     * @param currentGame current game.
     * @param gameWinner game winner.
     * @param ntrials current trial when game ended.
     * @param knockout true if one of the teams won by knockout.
     * @throws RemoteException A remote exception occurred.
     */
    void updateGameWinner(VectClock clientClock, int currentGame, int gameWinner, int ntrials, boolean knockout) throws RemoteException;

}
