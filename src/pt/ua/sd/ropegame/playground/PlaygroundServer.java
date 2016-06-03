package pt.ua.sd.ropegame.playground;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IPlayground;
import pt.ua.sd.ropegame.common.interfaces.IPlaygroundGenRep;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server that accepts to establish communication with clients in order to access {@link Playground}
 * Also instantiates a client to send messages to {@link pt.ua.sd.ropegame.genrepository.GeneralRepositoryServer}
 */
public class PlaygroundServer {

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
        int localPortNumber = configs.getPlaygroundPort();

        IPlaygroundGenRep playgroundGenRep = null;
        String genRepEntry = "GenRep";

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            playgroundGenRep = (IPlaygroundGenRep) registry.lookup(genRepEntry);
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
        Playground playground = new Playground(playgroundGenRep, configs);
        IPlayground playgroundInterface = null;

        try {
            playgroundInterface = (IPlayground) UnicastRemoteObject.exportObject(playground, localPortNumber);
        } catch (RemoteException e) {
            System.out.println("Exceção na geração do stub para o Terreiro: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O stub para o terreiro foi gerado.");

        String nameEntry = "Playground";
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
            registry.bind(nameEntry, playgroundInterface);
        } catch (RemoteException e) {
            System.out.println("Exceção no registo do terreiro: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O terreiro já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O terreiro foi registado.");

    }


}
