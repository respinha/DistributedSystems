package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import java.rmi.RemoteException;

/**
 * This interface contains all the methods an object must implement to allow interaction between the Coach and the Referee Site.
 */
public interface ICoachRefSite extends IRefSite {

    /**
     * Called by the last coach to arrive at the playground to inform the referee a new trial can start.
     * @param clientClock The coach's current clock.
     * @param teamID The coach's team.
     * @return Updated clock + new coach state.
     * @throws RemoteException
     */
    Response informReferee(VectClock clientClock, int teamID) throws RemoteException;

}
