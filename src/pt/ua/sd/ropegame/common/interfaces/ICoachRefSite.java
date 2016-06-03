package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface ICoachRefSite extends IRefSite {

    void informReferee(int teamID) throws RemoteException;

}
