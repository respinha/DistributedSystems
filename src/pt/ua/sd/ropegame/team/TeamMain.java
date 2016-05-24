package pt.ua.sd.ropegame.team;

import org.xml.sax.SAXException;
import pt.ua.sd.ropegame.common.DOMParser;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;

import java.io.IOException;

/**
 * Main class. Starts a team (Coach + contestants).
 */
public class TeamMain {

    /**
     * Starts a team.
     * @param args Configuration file + teamID (starts at 0).
     */
    public static void main(String... args) {

        if(args.length != 2)
            throw new IllegalArgumentException("Utilização: java -jar team <config.xml> <teamID (0 or 1)>");

        int team = Integer.parseInt(args[1]);
        DOMParser configs = null;

        try {
            configs = new DOMParser(args[0]);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        TeamClient teamClient = new TeamClient(team, new GameOfTheRopeConfigs(configs));


        teamClient.start();
        try {
            teamClient.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
