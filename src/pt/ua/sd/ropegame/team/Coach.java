package pt.ua.sd.ropegame.team;


import pt.ua.sd.ropegame.common.communication.Response;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.CoachStrategy;
import pt.ua.sd.ropegame.common.interfaces.*;

/**
 *  This class is an implementation of a Coach thread.<p>
 *
 *  The coach starts by waiting for the referee command to start a game.<p>
 *  After receiving this command, the coach picks his team according to an assigned strategy.<p>
 *  He then moves to playground, informs the referee the team is ready and waits for the trial to end.<p>
 *  This process is repeated until MAX_GAMES is reached.
 */
public class Coach extends TeamMember {

    // memory regions
    private ICoachBench bench;
    private ICoachPlay playground;
    private ICoachRefSite refSite;

    // current state
    private CoachState currentState;

    // coach strategies to follow during the game
    private CoachStrategy strategies;

    // this variable is set to true if a game ended due to a knockout
    private boolean knockout;

    private int[] clocks;


    /**
     * Constructor for Coach
     * @param team The team this coach belongs to
     * @param strategies The pool of strategies this coach follows during game
     */
    public Coach(ICoachBench bench, ICoachPlay playground, ICoachRefSite refSite, int team, CoachStrategy strategies) {
        super(team);

        this.strategies = strategies;

        // set memory regions
        this.bench = bench;
        this.playground = playground;
        this.refSite = refSite;

        knockout = false;
    }

    /**
     * Coach lifecycle
     */
    @Override
    public void run() {

        try {
            Response response = null;

            int currentTrial = 1;

            System.out.println("Now reviewing notes");
            // the coach is waiting for referee command
            bench.reviewNotes(this.team, currentTrial, knockout);

            while(bench.coachesHaveMoreOperations()) {

                System.out.println(currentState);

                switch (currentState) {

                    case WAIT_FOR_REFEREE_COMMAND:
                        try {
                            System.out.println("Waiting for coach call");
                            // wait for referee to call this coach

                            response = bench.waitForCoachCall();
                            clocks = response.getClocks();

                            System.out.println("Waiting for coach call2");
                            // call contestants
                            bench.callContestants(this.team, this.getStrategy().shortName());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;


                    case ASSEMBLE_TEAM:
                        try {

                            // the coach is moved to the playground
                            int iShouldInformRef = playground.moveCoachToPlayground(this.team);
                            // the last of the coaches informs the referee
                            // this variable is always set in the operation moveCoachToPlayground
                            if(iShouldInformRef == this.team)
                                refSite.informReferee(this.team);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;


                    case WATCH_TRIAL:
                        try {

                            response = playground.reviewNotes(this.team);
                            int result = response.getIntVal();
                            knockout = response.isBoolVal();
                            currentTrial = response.getInt2Val();

                            changeStrategy(result);

                            // update this team's contestants' strength
                            bench.reviewNotes(this.team, currentTrial+1, knockout);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }


            }

            System.out.println("O treinador da equipa "+ team + " terminou.");
            playground.closePlaygroundConnection();

        } catch (Exception e) {}
    }

    /**
     * Dinamically changes the coach strategy based on the last trial's result.
     * @param trialResult The last trial's result. 1 if this team won, -1 if this team lost, 0 if draw.
     */
    public void changeStrategy(int trialResult) {
        this.strategies.trialFinished(trialResult);
    }

    /**
     * @return The current strategy this coach is following
     */
    public CoachStrategy.Strategy getStrategy() {
        return strategies.getStrategy();
    }

    public void changeState(CoachState state) {
        this.currentState = state;
    }
}
