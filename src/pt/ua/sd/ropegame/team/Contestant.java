package pt.ua.sd.ropegame.team;


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
    public Contestant(IContestantsBench bench, IContestantsPlay playground, int team, int number) {
        super(team);

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
            response = bench.seatDown(number, team, strength, playgroundPos, false);
            currentState = ContestantState.longName(response.getState());


             do {
                 System.out.println(currentState);
                switch (currentState) {

                    case SEAT_AT_THE_BENCH:

                        try {
                            System.out.println("Seated");
                            // wait until the team's coach calls this contestant
                            response = bench.waitForContestantCall(this.number, this.team);
                            // clocks
                            strength = response.getIntVal();
                            hasMoreOper = response.isBoolVal();
                            if (!hasMoreOper) {
                                System.out.println("Eu, "+team+","+number+" vou morrer");
                                break;
                            }

                            System.out.println("Eu, "+number+" fui chamado");

                            // move to playground
                            response = playground.standInLine(this.number, this.team, this.strength);
                            //clocks
                            playgroundPos = response.getIntVal();
                            changeState(ContestantState.longName(response.getState()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case STAND_IN_POSITION:
                        try {
                            response = playground.getReady(number, team, strength);
                            // clocks
                            changeState(ContestantState.longName(response.getState()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DO_YOUR_BEST:
                        try {
                            response = playground.pullTheRope();
                            // clocks

                            response = playground.amDone();
                            // clocks

                            boolean matchOver = response.isBoolVal();

                            // seat down after the trial ended

                            System.out.println(matchOver);
                            response = bench.seatDown(number, team, strength, playgroundPos, matchOver);
                            System.out.println("Seat down finished");
                            System.out.println("Next state: "+response.getState());
                            // clocks

                            currentState = ContestantState.longName(response.getState());
                            playgroundPos = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        System.out.println("Cheguei aqui, mas não devia: "+number);
                        System.out.println(currentState);
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



    /*
    * Updates the contestant's current state
     */
    public void changeState(ContestantState state) {
        this.currentState = state;
    }

}
