package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefPlay extends IPlayground {

    Response startTrialPlayground(VectClock clientClock) throws RemoteException;
    Response assertTrialDecision(VectClock clientClock) throws InterruptedException, RemoteException;
    Response announceNewGamePlayground(VectClock clientClock) throws RemoteException;

}
