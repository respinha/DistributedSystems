package pt.ua.sd.ropegame.common.interfaces;


import java.rmi.RemoteException;

public interface IContestantsPlay extends ITeamPlayground {

    int standInLine(int gameMemberID, int teamID, int strength) throws RemoteException;
    void getReady(int gameMemberID, int teamID, int strength) throws InterruptedException, RemoteException;
    void pullTheRope() throws InterruptedException, RemoteException;
    boolean amDone() throws InterruptedException, RemoteException;

}
