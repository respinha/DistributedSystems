package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IContestantsBench extends ITeamBench {

    Response waitForContestantCall(VectClock clientClock, int gameMemberID, int teamID) throws InterruptedException, RemoteException;

    Response seatDown(VectClock clientClock, int gameMemberID, int teamID, int strength, int position, boolean gameOver) throws RemoteException;

    Response contestantsHaveMoreOperations() throws RemoteException;

    void closeBenchConnection() throws RemoteException;
}
