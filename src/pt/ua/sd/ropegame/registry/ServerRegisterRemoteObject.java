package pt.ua.sd.ropegame.registry;

import java.io.IOException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.xml.sax.SAXException;
import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IRegister;

/**
 *  This data type instantiates and registers a remote object that enables the registration of other remote objects
 *  located in the same or other processing nodes in the local registry service.
 *  Communication is based in Java RMI.
 */

public class ServerRegisterRemoteObject
{
    /**
     *  Main task.
     */

    public static void main(String[] args)
    {
    /* get location of the registry service */

        GameOfTheRopeConfigs configs = null;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        String rmiRegHostName = configs.getRmiHost();
        int rmiRegPortNumb = configs.getRmiPort();


    /* create and install the security manager */

        if (System.getSecurityManager () == null)
            System.setSecurityManager (new SecurityManager ());
        System.out.println ("Security manager was installed!");

    /* instantiate a registration remote object and generate a stub for it */

        RegisterRemoteObject regEngine = new RegisterRemoteObject(rmiRegHostName, rmiRegPortNumb);
        IRegister regEngineStub = null;
        int listeningPort = configs.getRegisterPort();                            /* it should be set accordingly in each case */

        try
        { regEngineStub = (IRegister) UnicastRemoteObject.exportObject (regEngine, listeningPort);
        }
        catch (RemoteException e)
        { System.out.println ("RegisterRemoteObject stub generation exception: " + e.getMessage ());
            System.exit (1);
        }
        System.out.println ("Stub was generated!");

    /* register it with the local registry service */

        String nameEntry = "RegisterHandler";
        Registry registry = null;

        try
        { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        }
        catch (RemoteException e)
        { System.out.println ("RMI registry creation exception: " + e.getMessage ());
            System.exit (1);
        }
        System.out.println ("RMI registry was created!");

        try
        { registry.rebind (nameEntry, regEngineStub);
        }
        catch (RemoteException e)
        { System.out.println ("RegisterRemoteObject remote exception on registration: " + e.getMessage ());
            System.exit (1);
        }
        System.out.println ("RegisterRemoteObject object was registered!");
    }
}
