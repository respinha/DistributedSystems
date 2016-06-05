package pt.ua.sd.ropegame.common;

import pt.ua.sd.ropegame.common.interfaces.IVectClock;
import pt.ua.sd.ropegame.referee.Referee;
import pt.ua.sd.ropegame.team.Coach;
import pt.ua.sd.ropegame.team.Contestant;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by rui on 6/4/16.
 */
public class VectClock  implements Serializable, Comparable<VectClock> {

    private static final long serialVersionUID = 201606042011L;

    private int[] clocks;
    private final int COACH_0_INDEX = 1;
    private final int COACH_1_INDEX = 7;

    public VectClock(GameOfTheRopeConfigs configs) {
        clocks = new int[configs.getNCoaches() + configs.getNContestants()*configs.getNTeams() + 1];
        for(int i = 0; i < clocks.length; i++) {
            clocks[i] = 0;
        }
    }

    public void increment(Object entity) {

        if(entity instanceof Referee)
            clocks[0]++;
        else if(entity instanceof Coach) {
            Coach c = (Coach) entity;
            int team = c.getTeam();

            if(team == 0) clocks[COACH_0_INDEX]++;
            else clocks[COACH_1_INDEX]++;
        } else {
            Contestant c = (Contestant) entity;
            int team = c.getTeam();
            int id = c.getNumber();

            if(team == 0) clocks[COACH_0_INDEX + id]++;
            else clocks[COACH_1_INDEX + id]++;
        }
    }

    public void update(VectClock clock) {

        int[] newClock = clock.copy();
        for(int i = 0; i < clocks.length; i++)
            if(clocks[i] < newClock[i]) clocks[i] = newClock[i];
    }

    private int[] copy() {
        int[] newArr = new int[this.clocks.length];
        for(int i = 0; i < newArr.length; i++) newArr[i] = clocks[i];

        return newArr;
    }

    @Override
    public int compareTo(VectClock o) {
        return sum(this) - sum(o);
    }

    private int sum(VectClock v) {

        int sum = 0;
        int[] clock = v.copy();
        for(int i = 0; i < clock.length; i++) sum += clock[i];

        return sum;
    }

    @Override
    public String toString() {
        return Arrays.toString(clocks);
    }
}
