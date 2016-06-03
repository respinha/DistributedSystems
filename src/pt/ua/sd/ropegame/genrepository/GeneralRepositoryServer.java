package pt.ua.sd.ropegame.genrepository;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.communication.ServerCom;
import pt.ua.sd.ropegame.common.communication.ClientProxy;
import pt.ua.sd.ropegame.common.interfaces.IGeneralRepository;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server that accepts to establish communication with clients in order to access {@link GeneralRepository}
 */
public class GeneralRepositoryServer {

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
        int localPortNumber = configs.getGenRepPort();

        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        System.out.println("Security Manager foi instalado.");

        String logFile = configs.getLogFileName();

        // shared regions and interfaces
        GeneralRepository generalRepository = new GeneralRepository(logFile);
        IGeneralRepository generalRepositoryInterface = null;

        try {
            generalRepositoryInterface = (IGeneralRepository) UnicastRemoteObject.exportObject(generalRepository, localPortNumber);
        } catch (RemoteException e) {
            System.out.println("Exceção na geração do stub para o Repositório Geral: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O stub para o repositório geral foi gerado.");

        String nameEntry = "GenRep";
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
            registry.bind(nameEntry, generalRepositoryInterface);
        } catch (RemoteException e) {
            System.out.println("Exceção no registo do repositório: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O repositório geral já está registado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("O repositório foi registado.");
    }



}
