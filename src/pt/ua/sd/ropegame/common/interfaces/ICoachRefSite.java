package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachRefSite extends IRefSite {

    Response informReferee(int teamID) throws RemoteException;

}
