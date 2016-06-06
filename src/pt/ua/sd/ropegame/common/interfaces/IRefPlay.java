package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * An interface that contains all the methods a Playground-like object must implement to allow a referee to connect with a remote playground.
 */
public interface IRefPlay extends IPlayground {

    /**
     * Signal a new trial is about to start.
     * @param clientClock Referee's current clock.
     * @return Updated clock + current trial + current state.
     * @throws RemoteException A remote exception occurred.
     */
    Response startTrialPlayground(VectClock clientClock) throws RemoteException;

    /**
     * Referee waits for all contestants to finish pulling the rope and updates general repository with rope position.
     * @param clientClock Referee's current clock.
     * @return Updated clock + true if game was ended due to knockout.
     * @throws InterruptedException The thread was interrupted.
     * @throws RemoteException A remote exception occurred.
     */
    Response assertTrialDecision(VectClock clientClock) throws InterruptedException, RemoteException;

    /**
     * Signal a new game is about to start.
     * @param clientClock Referee's current clock.
     * @return Updated clock.
     * @throws RemoteException A remote exception occurred.
     */
    Response announceNewGamePlayground(VectClock clientClock) throws RemoteException;

}
