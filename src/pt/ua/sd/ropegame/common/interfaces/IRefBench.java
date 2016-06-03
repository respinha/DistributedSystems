package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.RemoteException;

public interface IRefBench extends IBench {

    void callTrial() throws RemoteException;

    void notifyContestantsMatchIsOver() throws RemoteException;
}
