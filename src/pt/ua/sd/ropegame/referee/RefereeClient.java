package pt.ua.sd.ropegame.referee;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class starts the Referee Client.
 * The referee is responsible for game decisions concerning when to start and stop trials and declare game and trial winners.
 */
public class RefereeClient {

    public static void main(String... args) {
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

        String benchEntry = "Bench";
        String playgroundEntry = "Playground";
        String refSiteEntry = "RefSite";

        IRefBench benchInterface = null;
        IRefPlay playgroundInterface = null;
        IRefRefSite refereesiteInterface = null;


        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            benchInterface = (IRefBench) registry.lookup(benchEntry);
            playgroundInterface = (IRefPlay) registry.lookup(playgroundEntry);
            refereesiteInterface = (IRefRefSite) registry.lookup(refSiteEntry);
        } catch (RemoteException e) {
            System.out.println("Exceção na localização de um registo: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Um servidor não está registado: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        Referee referee = new Referee(configs, benchInterface, playgroundInterface, refereesiteInterface);
        referee.start();

        try {
            referee.join();
        } catch (InterruptedException e) {
            System.out.println("A thread do árbitro foi interrompida: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }
}
