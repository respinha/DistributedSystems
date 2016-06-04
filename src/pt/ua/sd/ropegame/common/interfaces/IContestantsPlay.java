package pt.ua.sd.ropegame.common.interfaces;


import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IContestantsPlay extends ITeamPlayground {

    Response standInLine(int gameMemberID, int teamID, int strength) throws RemoteException;
    Response getReady(int gameMemberID, int teamID, int strength) throws InterruptedException, RemoteException;
    Response pullTheRope() throws InterruptedException, RemoteException;
    Response amDone() throws InterruptedException, RemoteException;

}
