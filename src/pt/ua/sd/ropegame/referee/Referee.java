package pt.ua.sd.ropegame.referee;


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


    /**
     * Constructor for a referee.
     */
    public Referee(IRefBench bench, IRefPlay playground, IRefRefSite refSite) {

        // assign memory regions
        this.refereeSite = refSite;
        this.playground = playground;
        this.bench = bench;

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

                        response = playground.announceNewGamePlayground();
                        // clocks

                        response = this.refereeSite.announceNewGameRefSite();
                        currentState = RefereeState.longName(response.getState());
                        System.out.println("após: "+currentState);
                        // clocks
                        break;

                    case START_OF_A_GAME:
                        // call trial
                        response = bench.callTrial();
                        // clocks
                        currentState = RefereeState.longName(response.getState());

                        break;

                    case TEAMS_READY:
                        try {
                            // start trial when both teams are ready
                            response = refereeSite.startTrialRefSite();
                            // clocks
                            response = playground.startTrialPlayground();
                            // clocks

                            currentTrial = response.getIntVal();
                            currentState = RefereeState.longName(response.getState());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;

                    case WAIT_FOR_TRIAL_CONCLUSION:
                        try {

                            // wait for trial conclusion and update game stats
                            response = playground.assertTrialDecisionPlayground();

                            System.out.println("saiu astd");
                            // clocks
                            knockout = response.isBoolVal();
                            ropePos = response.getIntVal();
                            boolean endOfGame = response.isBoolVal2();

                            // response = refereeSite.assertTrialDecisionRefSite(currentTrial, knockout);


                            if (!endOfGame) {
                                response = bench.callTrial();
                                currentState = RefereeState.longName(response.getState());
                                // clocks
                            } else currentState = RefereeState.longName(response.getState());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case END_OF_A_GAME:

                        response = refereeSite.declareGameWinner(currentTrial, ropePos, knockout);

                        boolean endOfMatch = response.isBoolVal();

                        // clocks
                        if(!endOfMatch) {
                            playground.announceNewGamePlayground();
                            response = refereeSite.announceNewGameRefSite();
                            currentState = RefereeState.longName(response.getState());
                            // clocks
                        } else currentState = RefereeState.longName(response.getState());
                        break;

                    case END_OF_THE_MATCH:
                        // declare match winner

                        System.out.println("VOU MORRER AGORA");
                        response = refereeSite.declareMatchWinner();

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
