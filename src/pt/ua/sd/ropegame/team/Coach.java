package pt.ua.sd.ropegame.team;


import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.CoachStrategies;
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
    private CoachStrategies strategies;

    // this variable is set to true if a game ended due to a knockout
    private boolean knockout;


    /**
     * Constructor for Coach
     * @param team The team this coach belongs to
     * @param strategies The pool of strategies this coach follows during game
     */
    public Coach(ICoachBench bench, ICoachPlay playground, ICoachRefSite refSite, int team, CoachStrategies strategies) {
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

        int currentTrial = 1;

        // the coach is waiting for referee command
        bench.reviewNotes(this.team, currentTrial, knockout);

        while(bench.coachesHaveMoreOperations()) {

            switch (currentState) {

                case WAIT_FOR_REFEREE_COMMAND:
                    try {
                        // wait for referee to call this coach
                        bench.waitForCoachCall();
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

                        // wait until the trial has not finished
                        this.changeStrategy(playground.reviewNotes(this.team));
                        knockout = playground.isKnockout();
                        currentTrial = playground.getCurrentTrial();

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
    public CoachStrategies.Strategy getStrategy() {
        return strategies.getStrategy();
    }

    public void changeState(CoachState state) {
        this.currentState = state;
    }
}
