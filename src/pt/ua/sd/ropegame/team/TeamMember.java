package pt.ua.sd.ropegame.team;

/**
 * This is the superclass every coach and contestant inherit from.
 */
public class TeamMember extends Thread{

    protected int team;
    public TeamMember(int team) {
        this.team = team;
    }

    public int getTeam() {
        return this.team;
    }

}
