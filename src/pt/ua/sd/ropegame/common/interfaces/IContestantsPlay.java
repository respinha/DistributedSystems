package pt.ua.sd.ropegame.common.interfaces;


import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IContestantsPlay extends ITeamPlayground {

    Response standInLine(VectClock clientClock, int gameMemberID, int teamID, int strength) throws RemoteException;
    Response getReady(VectClock clientClock, int gameMemberID, int teamID, int strength) throws InterruptedException, RemoteException;
    Response pullTheRope(VectClock clientClock) throws InterruptedException, RemoteException;
    Response amDone(VectClock clientClock) throws InterruptedException, RemoteException;

}
