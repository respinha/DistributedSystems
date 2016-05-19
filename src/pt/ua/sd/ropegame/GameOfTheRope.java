package pt.ua.sd.ropegame;

import pt.ua.sd.ropegame.entities.Coach;
import pt.ua.sd.ropegame.entities.CoachStrategies;
import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.memregions.Bench;
import pt.ua.sd.ropegame.memregions.GeneralRepository;
import pt.ua.sd.ropegame.memregions.Playground;
import pt.ua.sd.ropegame.memregions.RefereeSite;

import pt.ua.sd.ropegame.utils.GameOfTheRopeConfigs;

/**
 *
 */
public class GameOfTheRope {

    public static void main(String... args) {

        for (int z = 0; z < 10; z++) {


            // create general repository
            GeneralRepository repository = new GeneralRepository();


            // create memory regions
            Bench b = new Bench(repository);
            Playground playground = new Playground(repository);
            RefereeSite rs = new RefereeSite(repository);

            // create game entities
            Referee referee = new Referee(b, rs, playground);

            Coach[] coaches = new Coach[GameOfTheRopeConfigs.N_COACHES];
            Contestant[][] teamContestants = new Contestant[GameOfTheRopeConfigs.N_TEAMS][GameOfTheRopeConfigs.N_TEAM_CONTESTANTS];

            for (int i = 0; i < GameOfTheRopeConfigs.N_TEAMS; i++)
                coaches[i] = new Coach(b, playground, rs, i, new CoachStrategies());

            for (int i = 0; i < GameOfTheRopeConfigs.N_TEAMS; i++) {

                for (int j = 0; j < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; j++)
                    teamContestants[i][j] = new Contestant(b, playground, i, j);

            }

            // assign coaches and contestants to bench
            b.assignCoaches(coaches);
            b.assignContestants(teamContestants);

            // start threads

            referee.start();

            for (int i = 0; i < GameOfTheRopeConfigs.N_COACHES; i++)
                coaches[i].start();
            for (int i = 0; i < GameOfTheRopeConfigs.N_TEAMS; i++) {

                for (int j = 0; j < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; j++)
                    teamContestants[i][j].start();
            }

            try {
                referee.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < GameOfTheRopeConfigs.N_COACHES; i++)
                try {
                    coaches[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            for (int i = 0; i < GameOfTheRopeConfigs.N_TEAMS; i++) {
                for (int j = 0; j < GameOfTheRopeConfigs.N_TEAM_CONTESTANTS; j++)
                    try {
                        teamContestants[i][j].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
        }
    }
}
