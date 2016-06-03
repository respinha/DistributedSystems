package pt.ua.sd.ropegame.bench;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IBench;
import pt.ua.sd.ropegame.common.interfaces.IBenchGenRep;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server that accepts to establish communication with clients in order to access {@link Bench}
 * Also instantiates a client to send messages to {@link pt.ua.sd.ropegame.genrepository.GeneralRepositoryServer}
 */
public class BenchServer {

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
        int localPortNumber = configs.getBenchPort();


        IBenchGenRep benchGenRep = null;
        String genRepEntry = "GenRep";

        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            benchGenRep= (IBenchGenRep) registry.lookup(genRepEntry);
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
        Bench bench = new Bench(benchGenRep, configs);
        IBench benchInterface = null;

        try {
            benchInterface = (IBench) UnicastRemoteObject.exportObject(bench, localPortNumber); 
        } catch (RemoteException e) {
            System.out.println("Exceção na geração do stub para o Banco: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O stub para o banco foi gerado.");

        String nameEntry = "Bench";
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
            registry.bind(nameEntry, benchInterface);
        } catch (RemoteException e) {
            System.out.println("Exceção no registo do banco: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O banco já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O banco foi registado.");

    }

}
