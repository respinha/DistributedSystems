package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface ICoachBench extends ITeamBench {

    void reviewNotes(int teamID, int trial, boolean knockout) throws RemoteException;

    void callContestants(int teamID, String strategy) throws RemoteException;

    void waitForCoachCall() throws InterruptedException, RemoteException;

    boolean coachesHaveMoreOperations() throws RemoteException;
}
