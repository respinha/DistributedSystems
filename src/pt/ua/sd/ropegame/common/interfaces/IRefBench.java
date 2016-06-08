package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * An interface every bench-like object must implement to allow a referee to connect to a remote bench.
 */
public interface IRefBench extends IBench {

    /**
     * Called by the referee to signal a new trial is about to start.
     * @param clientClock The client's current clock.
     * @return Updated clock.
     * @throws RemoteException A remote exception has occurred.
     */
    Response callTrial(VectClock clientClock) throws RemoteException;

}
