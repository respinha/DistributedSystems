package pt.ua.sd.ropegame.common.interfaces;


import java.rmi.RemoteException;

public interface ICoachPlay extends ITeamPlayground {

    int getCurrentTrial() throws RemoteException;

    int reviewNotes(int teamID) throws InterruptedException, RemoteException;

    int moveCoachToPlayground(int teamID) throws InterruptedException, RemoteException;

    boolean isKnockout() throws RemoteException;

    boolean closePlaygroundConnection() throws RemoteException;
}
