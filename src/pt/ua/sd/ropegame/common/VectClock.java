package pt.ua.sd.ropegame.common;

import pt.ua.sd.ropegame.common.interfaces.IClockChangeListener;
import pt.ua.sd.ropegame.referee.Referee;
import pt.ua.sd.ropegame.team.Coach;
import pt.ua.sd.ropegame.team.Contestant;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A vectorial clock that contains all the methods to create, update and clone a clock.
 */
public class VectClock  implements Serializable, Comparable<VectClock> {

    private static final long serialVersionUID = 201606042011L;

    private int[] clocks;
    private final int COACH_0_INDEX = 1;
    private final int COACH_1_INDEX = 7;

    private IClockChangeListener listener;

    /**
     * Constructor for a Vectorial Clock.
     * @param configs Game of the Rope configuration.
     */
    public VectClock(GameOfTheRopeConfigs configs) {
        clocks = new int[configs.getNCoaches() + configs.getNContestants()*configs.getNTeams() + 1];
        for(int i = 0; i < clocks.length; i++) {
            clocks[i] = 0;
        }
    }

    /**
     * Assign an object as a listener to clock changing events.
     * @param listener The event listener.
     */
    public void assignClockListener(IClockChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Increment the clock value for a given entity.
     * @param entity The entity whose clock we're incrementing.
     */
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

            if(team == 0) clocks[COACH_0_INDEX + id + 1]++;
            else clocks[COACH_1_INDEX + id + 1]++;
        }
    }

    /**
     * Update a clock.
     * @param clock The new clock to compare this instance to.
     */
    public void update(VectClock clock) {

        int[] newClock = clock.copy();
        for(int i = 0; i < clocks.length; i++)
            if(clocks[i] < newClock[i]) clocks[i] = newClock[i];

        if(listener != null)
            listener.clockUpdated();
    }

    /**
     * Copy this clock.
     * @return A copy of this clock.
     */
    private int[] copy() {
        int[] newArr = new int[this.clocks.length];
        for(int i = 0; i < newArr.length; i++) newArr[i] = clocks[i];

        return newArr;
    }

    /**
     * Compare a clock object to another.
     * @param o another clock object.
     * @return Sorted clocks.
     */
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

    /**
     * Get clocks as an int array.
     * @return vectorial clock as an int array.
     */
    public int[] getClocks() {
        return clocks;
    }
}
