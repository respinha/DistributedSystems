package pt.ua.sd.ropegame.refereesite;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IRefSite;
import pt.ua.sd.ropegame.common.interfaces.IRefSiteGenRep;
import pt.ua.sd.ropegame.common.interfaces.IRegister;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RefereeSite server module that connects to a GeneralRepository instance.
 */
public class RefereeSiteServer {


    public static void main(String[] args) {
        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        String rmiRegHostName = configs.getRmiHost();
        int rmiRegPortNumb = configs.getRmiPort();
        int localPortNumber = configs.getRefSitePort();

        IRefSiteGenRep refSiteGenRep = null;
        String genRepEntry = "GenRep";

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            refSiteGenRep = (IRefSiteGenRep) registry.lookup(genRepEntry);
        } catch (RemoteException e) {
            System.out.println("Exceção na localização de um registo: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Um servidor não está registado: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }


        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        System.out.println("Security Manager foi instalado.");


        // shared regions and interfaces
        RefereeSite refereeSite = new RefereeSite(refSiteGenRep, configs);
        IRefSite refSiteInterface = null;

        try {
            refSiteInterface = (IRefSite) UnicastRemoteObject.exportObject(refereeSite, localPortNumber);
        } catch (RemoteException e) {
            System.out.println("Exceção na geração do stub para a Zona do árbitro: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O stub para a zona do árbitro foi gerado.");

        String nameEntry = "RefSite";
        IRegister register = null;
        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("Exceção na geração do registo RMI: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O registo RMI foi criado.");

        try {
            register = (IRegister) registry.lookup(nameEntry);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage ());
            e.printStackTrace ();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage ());
            e.printStackTrace();
            System.exit(1);
        }


        try {
            register.bind(nameEntry, refSiteInterface);
        } catch (RemoteException e) {
            System.out.println("Exceção no registo da zona do árbitro: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("A zona do árbitro já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("A zona do árbitro foi registada.");


    }

}
