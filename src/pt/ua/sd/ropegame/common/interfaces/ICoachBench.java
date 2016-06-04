package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachBench extends ITeamBench {

    Response reviewNotes(VectClock clientClock, int teamID, int trial, boolean knockout) throws RemoteException;

    Response callContestants(VectClock clientClock, int teamID, String strategy) throws RemoteException;

    Response waitForCoachCall(VectClock clientClock) throws InterruptedException, RemoteException;

    Response coachesHaveMoreOperations() throws RemoteException;

}
