package pt.ua.sd.ropegame.referee;


import pt.ua.sd.ropegame.common.interfaces.IRefSite;
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
    private IRefSite refereeSite;


    private int currentTrial;

    // position of the rope and information on knockout
    private int ropePos;
    private boolean knockout;


    /**
     * Constructor for a referee.
     */
    public Referee(IRefBench bench, IRefPlay playground, IRefSite refSite) {

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

        refereeSite.startTheMatch();

        while(refereeSite.refHasMoreOperations()) {
            switch (currentState) {

                case START_OF_THE_MATCH:                    // transition

                    this.playground.announceNewGamePlayground();
                    this.refereeSite.announceNewGameRefSite();

                    break;

                case START_OF_A_GAME:
                    // call trial
                    bench.callTrial();

                    break;

                case TEAMS_READY:
                    try {
                        // start trial when both teams are ready
                        refereeSite.startTrialRefSite();
                        currentTrial = playground.startTrialPlayground();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;

                case WAIT_FOR_TRIAL_CONCLUSION:
                    try {

                        // wait for trial conclusion and update game stats
                        knockout = this.playground.assertTrialDecisionPlayground();
                        ropePos = this.playground.getRopePos();

                        boolean endOfGame = refereeSite.assertTrialDecisionRefSite(currentTrial, knockout);

                        if(!endOfGame) {
                            bench.callTrial();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case END_OF_A_GAME:


                    boolean endOfMatch = refereeSite.declareGameWinner(currentTrial, ropePos, knockout);

                    if(endOfMatch)
                        bench.notifyContestantsMatchIsOver();

                    else {
                        playground.announceNewGamePlayground();
                        refereeSite.announceNewGameRefSite();
                    }
                    break;

                case END_OF_THE_MATCH:
                    // declare match winner

                    refereeSite.declareMatchWinner();
                    break;
            }
        }

        System.out.println("O árbitro terminou.");
        refereeSite.closeRefSite();

    }

    /**
     * Change referee state.
     * @param state The referee's new state.
     */
    public void changeState(RefereeState state) {
        this.currentState = state;
    }

}
