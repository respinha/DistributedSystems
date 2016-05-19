package pt.ua.sd.ropegame.entities;

import genclass.GenericIO;
import pt.ua.sd.ropegame.enums.RefereeState;
import pt.ua.sd.ropegame.interfaces.IRefBench;
import pt.ua.sd.ropegame.interfaces.IRefPlay;
import pt.ua.sd.ropegame.interfaces.IRefSite;
import pt.ua.sd.ropegame.interfaces.IRefereeGenRep;


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

    private IRefereeGenRep repository;

    private int currentGame;
    private int currentTrial;

    // position of the rope and information on knockout
    private int ropePos;
    private boolean knockout;
    private boolean endOfOperations;


    /**
     * Constructor for a referee.
     */
    public Referee(IRefBench bench, IRefSite refSite, IRefPlay playground) {
        // repository.setRef(this);

        // assign memory regions

        this.bench = bench;
        this.refereeSite = refSite;
        this.playground = playground;
        // init variables
        currentTrial = 0;
        currentGame = 0;
        ropePos = 0;

        knockout = false;

        endOfOperations = false;

    }


    /**
     * Referee's lifecycle.
     */
    @Override
    public void run() {

        refereeSite.startTheMatch(this);

        while(refereeSite.refHasMoreOperations()) {
            switch (currentState) {

                case START_OF_THE_MATCH:                    // transition

                    try {
                        // announce a new game
                        this.playground.announceNewGame();
                        this.refereeSite.announceNewGame(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case START_OF_A_GAME:

                    try {
                        // call trial
                        bench.callTrial(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case TEAMS_READY:
                    try {
                        // start trial when both teams are ready
                        refereeSite.startTrial();
                        currentTrial = playground.startTrial(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;

                case WAIT_FOR_TRIAL_CONCLUSION:
                    try {

                        // wait for trial conclusion and update game stats
                        knockout = this.playground.assertTrialDecision();
                        ropePos = this.playground.getRopePos();

                        boolean endOfGame = !refereeSite.assertTrialDecision(this, currentTrial, ropePos, knockout);

                        if(!endOfGame) {
                            bench.callTrial(this);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case END_OF_A_GAME:


                    boolean endOfMatch = refereeSite.declareGameWinner(this, currentTrial, knockout);

                    if(endOfMatch)
                        bench.notifyContestantsMatchIsOver();
                    else {
                        try {
                            playground.announceNewGame();
                            refereeSite.announceNewGame(this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case END_OF_THE_MATCH:
                    // declare match winner

                    refereeSite.declareMatchWinner(this);
                    break;

            }



        }

    }

    public void changeState(RefereeState state) {
        this.currentState = state;
    }

    public void hasNoMoreOperations() {
        this.endOfOperations = true;
    }
}
