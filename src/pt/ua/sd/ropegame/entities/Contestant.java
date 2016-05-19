package pt.ua.sd.ropegame.entities;

import genclass.GenericIO;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.interfaces.IContestantGenRep;
import pt.ua.sd.ropegame.interfaces.IContestantsBench;
import pt.ua.sd.ropegame.interfaces.IContestantsPlay;

import java.util.Random;

/**
 *  This class is an implementation of a Contestant thread.<p>
 *
 *  The contestant starts by waiting seated at the bench until he's picked by the coach.<p>
 *  The last contestant of a team to join the playground informs the coach that the full team is already on the playground and thus the trial can begin.<p>
 *  During the trial, every contestant pulls the rope and seats down afterward.<p>
 *  This process is repeated until MAX_GAMES is reached.
 */
public class Contestant extends TeamMember {

    // contestant information
    private int number;
    private int strength;
    private ContestantState currentState;
    private boolean picked;
    private int playgroundPos;
    private boolean playedInLastTrial;

    // memory regions
    private IContestantsBench bench;
    private IContestantsPlay playground;


    private boolean endOfOperations;


    private final static Random RANDOMGEN = new Random();

    /**
     * Constructor for a contestant.
     * @param team The team this contestant belongs to.
     * @param number This contestant's number.
     */
    public Contestant(IContestantsBench bench, IContestantsPlay playground, int team, int number) {
        super(team);

        // generate a random strength value
        this.strength = RANDOMGEN.nextInt(5-1)+1;

        this.number = number;

        // assign memory regions
        this.bench = bench;
        this.playground = playground;

        playgroundPos = 0;
        playedInLastTrial = false;
        picked = false;

        endOfOperations = false;
    }



    @Override
    public void run() {

        bench.seatDown(this, number, team, strength, playgroundPos);

        while(bench.contestantsHaveMoreOperations()) {

            switch(currentState) {

                case SEAT_AT_THE_BENCH:

                    try {
                        // wait until the team's coach calls this contestant
                        strength = bench.waitForContestantCall(number, team);

                        if(endOfOperations) break;

                        // move to playground
                        playgroundPos = playground.standInLine(this, number, team, strength);
                    } catch (InterruptedException e) {
                         e.printStackTrace();
                    }
                    break;

                case STAND_IN_POSITION:
                    try {
                        playground.getReady(this, number, team, strength);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case DO_YOUR_BEST:
                    try {

                        playground.pullTheRope();
                        playground.amDone();

                        // seat down after the trial ended
                        bench.seatDown(this, number, team, strength, playgroundPos);

                        playgroundPos = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }


        }

    }

    /**
     * @return true if contestant played in last trial, false otherwise.
     */
    public boolean playedInLastTrial() {
        return playedInLastTrial;
    }


    /**
     * Sets the variable playedInLastTrial to true.
     */
    public void hasPlayedInLastTrial() {
        this.playedInLastTrial = true;
    }

    /**
     * @return This contestant's playground position.
     */
    public int getPlaygroundPos() {
        return this.playgroundPos;
    }

    /**
     * Call contestant.
     * @param pick true if the contestant was picked by the coach.
     */
    public void callContestant(boolean pick)  {

        picked = pick;
    }


    /**
     * @return true if the contestant was picked by the coach.
     */
    public boolean wasPicked() {
        return picked;
    }


    /**
     * @return This contestant's strength.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Update contestant strength.
     */
    public void updateStrength() {
        if (playedInLastTrial) {
            if(this.strength > 0)
                this.strength -= 1;

            playedInLastTrial = false;

        }
        else
            this.strength += 1;
    }

    /**
     * @return This contestant's number.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Assign a playground position to this contestant.
     * @param playgroundPos Position this contestant occupies in the playground.
     */
    public void assignPosition(int playgroundPos) {
        this.playgroundPos = playgroundPos;
    }

    /**
     * Removes contestant from playground by assigning zero to its position,
     */
    public void removePlaygroundPosition() {
        this.playgroundPos = 0;
    }

    /*
    * Updates the contestant's current state
     */
    public void changeState(ContestantState state) {
        this.currentState = state;
    }

    /**
     * Assigns true to endOfOperations, in order to end the contestants' lifecycle
     */
    public void hasNoMoreOperations() {
        endOfOperations = true;
    }
}
