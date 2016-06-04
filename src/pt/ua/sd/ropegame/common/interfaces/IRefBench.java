package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Response;

import java.rmi.RemoteException;

public interface IRefBench extends IBench {

    Response callTrial() throws RemoteException;

}
