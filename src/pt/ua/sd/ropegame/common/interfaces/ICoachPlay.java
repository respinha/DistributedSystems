package pt.ua.sd.ropegame.common.interfaces;


import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachPlay extends ITeamPlayground {

    Response getCurrentTrial() throws RemoteException;

    Response reviewNotes(int teamID) throws InterruptedException, RemoteException;

    Response moveCoachToPlayground(int teamID) throws InterruptedException, RemoteException;

    Response isKnockout() throws RemoteException;

    void closePlaygroundConnection() throws RemoteException;
}
