package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachBench extends ITeamBench {

    Response reviewNotes(int teamID, int trial, boolean knockout) throws RemoteException;

    Response callContestants(int teamID, String strategy) throws RemoteException;

    Response waitForCoachCall() throws InterruptedException, RemoteException;

    Response coachesHaveMoreOperations() throws RemoteException;

}
