package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by rui on 6/8/16.
 */
public interface IRegister extends Remote {

    void bind (String name, Remote ref) throws RemoteException, AlreadyBoundException;

    void unbind(String name) throws RemoteException, NotBoundException;

    void rebind(String name, Remote ref) throws RemoteException;
}
