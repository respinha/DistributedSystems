package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefPlay extends IPlayground {

    Response startTrialPlayground() throws RemoteException;
    Response assertTrialDecisionPlayground() throws InterruptedException, RemoteException;
    Response announceNewGamePlayground() throws RemoteException;

}
