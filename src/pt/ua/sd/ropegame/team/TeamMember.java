package pt.ua.sd.ropegame.team;

import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;

/**
 * This is the superclass every coach and contestant inherit from.
 */
public class TeamMember extends Thread{

    protected int team;
    protected VectClock clock;

    public TeamMember(GameOfTheRopeConfigs configs, int team) {
        this.team = team;
        clock = new VectClock(configs);
    }

    public int getTeam() {
        return this.team;
    }

}
