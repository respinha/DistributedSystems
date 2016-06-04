package pt.ua.sd.ropegame.referee;


import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.communication.Response;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.interfaces.IRefRefSite;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.IRefBench;
import pt.ua.sd.ropegame.common.interfaces.IRefPlay;


/**
 *  This class is an implementation of a Referee thread.<p>
 *
 *  The referee starts by announcing a new game and then a new trial.<p>
 *  When all contestants and coaches are ready, the referee starts a new trial.<p>
 *  The referee is responsible for announcing which team won each trial, game and match.<p>
 *  This process is repeated until MAX_GAMES is reached.
 */
public class Referee extends Thread {

    private RefereeState currentState;

    // memory regions
    private IRefBench bench;
    private IRefPlay playground;
    private IRefRefSite refereeSite;

    private int currentTrial;

    // position of the rope and information on knockout
    private int ropePos;
    private boolean knockout;

    private VectClock clock;


    /**
     * Constructor for a referee.
     */
    public Referee(GameOfTheRopeConfigs configs, IRefBench bench, IRefPlay playground, IRefRefSite refSite) {

        // assign memory regions
        this.refereeSite = refSite;
        this.playground = playground;
        this.bench = bench;
        clock = new VectClock(configs);

        // init variables
        currentTrial = 0;
        ropePos = 0;

        knockout = false;
    }


    /**
     * Referee's lifecycle.
     */
    @Override
    public void run() {

        try {
            refereeSite.startTheMatch();
            Response response;

            boolean hasMoreOper;
            currentState = RefereeState.START_OF_THE_MATCH;

            do {
                System.out.println(currentState);
                switch (currentState) {

                    case START_OF_THE_MATCH:                    // transition

                        clock.increment(this);
                        response = playground.announceNewGamePlayground(clock);
                        clock.update(response.getClock());

                        clock.increment(this);
                        response = this.refereeSite.announceNewGameRefSite(clock);
                        clock.update(response.getClock());
                        currentState = RefereeState.longName(response.getState());
                        break;

                    case START_OF_A_GAME:
                        clock.increment(this);
                        response = bench.callTrial(clock);
                        clock.update(response.getClock());

                        currentState = RefereeState.longName(response.getState());

                        break;

                    case TEAMS_READY:
                        try {
                            // start trial when both teams are ready

                            clock.increment(this);
                            response = refereeSite.startTrialRefSite(clock);
                            clock.update(response.getClock());

                            clock.increment(this);
                            response = playground.startTrialPlayground(clock);
                            clock.update(response.getClock());

                            currentTrial = response.getIntVal();
                            currentState = RefereeState.longName(response.getState());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;

                    case WAIT_FOR_TRIAL_CONCLUSION:
                        try {

                            // wait for trial conclusion and update game stats
                            clock.increment(this);
                            response = playground.assertTrialDecision(clock);
                            clock.update(response.getClock());

                            knockout = response.isBoolVal();
                            ropePos = response.getIntVal();
                            boolean endOfGame = response.isBoolVal2();


                            if (!endOfGame) {
                                clock.increment(this);
                                response = bench.callTrial(clock);
                                clock.update(response.getClock());

                                currentState = RefereeState.longName(response.getState());
                                // clocks
                            } else currentState = RefereeState.longName(response.getState());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case END_OF_A_GAME:
                        clock.increment(this);
                        response = refereeSite.declareGameWinner(clock, currentTrial, ropePos, knockout);
                        clock.update(response.getClock());

                        boolean endOfMatch = response.isBoolVal();

                        // clocks
                        if(!endOfMatch) {
                            clock.increment(this);
                            playground.announceNewGamePlayground(clock);
                            clock.update(response.getClock());

                            clock.increment(this);
                            response = refereeSite.announceNewGameRefSite(clock);
                            clock.update(response.getClock());

                            currentState = RefereeState.longName(response.getState());
                            // clocks
                        } else currentState = RefereeState.longName(response.getState());
                        break;

                    case END_OF_THE_MATCH:
                        // declare match winner

                        clock.increment(this);
                        response = refereeSite.declareMatchWinner(clock);
                        clock.update(response.getClock());

                        // clocks
                        break;
                }

                response = refereeSite.refHasMoreOperations();
                hasMoreOper = response.isBoolVal();
                System.out.println("Arbitro: " + hasMoreOper);
                // clocks
            } while (hasMoreOper);

            System.out.println("O árbitro terminou.");
            refereeSite.closeRefSite();

        } catch (Exception e) {}

    }

    /**
     * Change referee state.
     * @param state The referee's new state.
     */
    public void changeState(RefereeState state) {
        this.currentState = state;
    }

}
