package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface ICoachBench extends ITeamBench {

    void reviewNotes(int teamID, int trial, boolean knockout) throws RemoteException;

    void callContestants(int teamID, String strategy) throws RemoteException;

    Response waitForCoachCall() throws InterruptedException, RemoteException;

    boolean coachesHaveMoreOperations() throws RemoteException;

}
