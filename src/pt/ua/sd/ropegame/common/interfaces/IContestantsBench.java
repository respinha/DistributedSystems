package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IContestantsBench extends ITeamBench {

    int waitForContestantCall(int gameMemberID, int teamID) throws InterruptedException, RemoteException;

    void seatDown(int gameMemberID, int teamID, int strength, int position, boolean gameOver) throws RemoteException;

    boolean contestantsHaveMoreOperations() throws RemoteException;

    boolean closeBenchConnection() throws RemoteException;
}
