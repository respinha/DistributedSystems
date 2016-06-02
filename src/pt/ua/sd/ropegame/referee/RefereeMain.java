package pt.ua.sd.ropegame.referee;


import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;

/**
 * This class starts the Referee Client.
 * The referee is responsible for game decisions concerning when to start and stop trials and declare game and trial winners.
 */
public class RefereeMain {

    public static void main(String... args) {
        if(args.length != 1)
            throw new IllegalArgumentException("Utilização: java -jar refereesite <config.xml>");

        GameOfTheRopeConfigs configs;
        try {
            configs = new GameOfTheRopeConfigs(new DOMParser(args[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ocorreu um erro ao carregar o ficheiro de configuração.");
        }

        IRefClient refClient = new RefClient(configs);
        refClient.start();

        try {
            refClient.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("O cliente árbitro terminou.");
        }
    }
}
