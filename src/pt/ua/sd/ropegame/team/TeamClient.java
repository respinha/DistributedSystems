package pt.ua.sd.ropegame.team;

import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.enums.CoachStrategy;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main class. Starts a team (Coach + contestants).
 */
public class TeamClient {

    /**
     * Starts a team.
     * @param args Configuration file + teamID (starts at 0).
     */
    public static void main(String... args) {

        if(args.length != 2)
            throw new IllegalArgumentException("Utilização: java -jar team <config.xml> <teamID (0 or 1)>");

        int team = Integer.parseInt(args[1]);

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

        ITeamBench bench = null;
        ITeamPlayground playground = null;
        ICoachRefSite refereeSite = null;


        try {
            Registry registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
            bench = (ITeamBench) registry.lookup(benchEntry);
            playground = (ITeamPlayground) registry.lookup(playgroundEntry);
            refereeSite = (ICoachRefSite) registry.lookup(refSiteEntry);
        } catch (RemoteException e) {
            System.out.println("Exceção na localização de um registo: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Um servidor não está registado: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        Coach coach = new Coach((ICoachBench) bench, (ICoachPlay) playground, refereeSite, team, new CoachStrategy());
        Contestant[] contestants = new Contestant[configs.getNContestants()];

        for (int j = 0; j < configs.getNContestants(); j++)
            contestants[j] = new Contestant((IContestantsBench) bench, (IContestantsPlay) playground, team, j);


        coach.start();

        for(int i = 0; i < configs.getNContestants(); i++)
            contestants[i].start();

        try {
            coach.join();


            for (int i = 0; i < configs.getNContestants(); i++)
                contestants[i].join();
        } catch (InterruptedException e) {
            System.out.println("Uma thread foi interrompida: "+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }
}
