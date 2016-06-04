package pt.ua.sd.ropegame.team;


import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import pt.ua.sd.ropegame.common.interfaces.IContestantsPlay;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.interfaces.IContestantsBench;

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
    private int playgroundPos;

    // memory regions
    private IContestantsBench bench;
    private IContestantsPlay playground;

    private final static Random RANDOMGEN = new Random();

    /**
     * Constructor for a contestant.
     * @param team The team this contestant belongs to.
     * @param number This contestant's number.
     */
    public Contestant(GameOfTheRopeConfigs configs, IContestantsBench bench, IContestantsPlay playground, int team, int number) {
        super(configs, team);

        // generate a random strength value
        this.strength = RANDOMGEN.nextInt(5-1)+1;

        this.number = number;

        // assign memory regions
        this.bench = bench;
        this.playground = playground;

        playgroundPos = 0;
    }



    @Override
    public void run() {

        Response response;
        boolean hasMoreOper;
        try {
            clock.increment(this);
            response = bench.seatDown(clock, number, team, strength, playgroundPos, false);
            clock.update(response.getClock());

            currentState = ContestantState.longName(response.getState());

            do {
                System.out.println(currentState);
                switch (currentState) {

                    case SEAT_AT_THE_BENCH:

                        try {
                            // wait until the team's coach calls this contestant
                            clock.increment(this);
                            response = bench.waitForContestantCall(clock, this.number, this.team);
                            clock.update(response.getClock());

                            strength = response.getIntVal();
                            hasMoreOper = response.isBoolVal();
                            if (!hasMoreOper)
                                break;

                            // move to playground
                            clock.increment(this);
                            response = playground.standInLine(clock, this.number, this.team, this.strength);
                            clock.update(response.getClock());

                            playgroundPos = response.getIntVal();
                            changeState(ContestantState.longName(response.getState()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case STAND_IN_POSITION:
                        try {
                            clock.increment(this);
                            response = playground.getReady(clock, number, team, strength);
                            clock.update(response.getClock());

                            changeState(ContestantState.longName(response.getState()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DO_YOUR_BEST:
                        try {
                            clock.increment(this);
                            response = playground.pullTheRope(clock);
                            clock.update(response.getClock());

                            clock.increment(this);
                            response = playground.amDone(clock);
                            clock.update(response.getClock());

                            boolean matchOver = response.isBoolVal();

                            // seat down after the trial ended

                            System.out.println(matchOver);

                            clock.increment(this);
                            response = bench.seatDown(clock, number, team, strength, playgroundPos, matchOver);
                            clock.update(response.getClock());

                            currentState = ContestantState.longName(response.getState());
                            playgroundPos = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                }


                response = bench.contestantsHaveMoreOperations();
                hasMoreOper = response.isBoolVal();
                System.out.println("Contestant: " + number + ", " + hasMoreOper);
            } while (hasMoreOper);

            System.out.println("O jogador " + number + " da equipa " + team + " terminou.");
            bench.closeBenchConnection();
        } catch (Exception e) {}
    }

    public int getNumber() {
        return number;
    }


    /*
    * Updates the contestant's current state
     */
    public void changeState(ContestantState state) {
        this.currentState = state;
    }

}
