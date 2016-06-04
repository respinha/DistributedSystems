package pt.ua.sd.ropegame.common.interfaces;


import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachPlay extends ITeamPlayground {

    Response reviewNotes(VectClock clientClock, int teamID) throws InterruptedException, RemoteException;

    Response moveCoachToPlayground(VectClock clientClock, int teamID) throws InterruptedException, RemoteException;

    void closePlaygroundConnection() throws RemoteException;
}
