package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * An interface every referee Site-like object must implement to allow a referee to interact with a remote ref site.
 */
public interface IRefRefSite extends IRefSite {

    /**
     * Announce a new game.
     * @param clientClock Referee's current clock.
     * @return Updated clock + new ref state.
     * @throws RemoteException A remote exception has occurred.
     */
    Response announceNewGameRefSite(VectClock clientClock) throws RemoteException;

    /**
     * Declare a team won the game.
     * @param clientClock Referee's current clock.
     * @param ntrials Current number of trials.
     * @param ropePos Rope position.
     * @param knockout True if game ended due to knockout.
     * @return Updated clock + match ended (boolean)
     * @throws RemoteException A remote exception has occurred.
     */
    Response declareGameWinner(VectClock clientClock, int ntrials, int ropePos, boolean knockout) throws RemoteException;

    /**
     * Declare a team won the match.
     * @param clientClock Referee's current clock.
     * @return Updated clock.
     * @throws RemoteException A remote exception has occurred.
     */
    Response declareMatchWinner(VectClock clientClock) throws RemoteException;

    /**
     * Block the referee until the last coach informs her all contestants are ready.
     * @param clientClock Referee's current clock.
     * @return Updated clock.
     * @throws InterruptedException The thread was interrupted.
     * @throws RemoteException A remote exception has occurred.
     */
    Response startTrialRefSite(VectClock clientClock) throws InterruptedException, RemoteException;

    /**
     * Signal a new match is about to start.
     * @param clientClock Referee's current clock.
     * @return Updated clock + new status.
     * @throws RemoteException A remote exception has occurred.
     */
    Response startTheMatch(VectClock clientClock) throws RemoteException;

    /**
     * Checks if a referee has more operations.
     * @return True if referee has more operations; false otherwise.
     * @throws RemoteException A remote exception has occurred.
     */
    Response refHasMoreOperations() throws RemoteException;

    /**
     * Close a connection to a remote referee site.
     * @throws RemoteException A remote exception has occurred.
     */
    void closeRefSite() throws RemoteException;
}
