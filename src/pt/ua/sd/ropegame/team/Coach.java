package pt.ua.sd.ropegame.team;


import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
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


    /**
     * Constructor for Coach
     * @param team The team this coach belongs to
     * @param strategies The pool of strategies this coach follows during game
     */
    public Coach(GameOfTheRopeConfigs configs, ICoachBench bench, ICoachPlay playground, ICoachRefSite refSite, int team, CoachStrategy strategies) {
        super(configs, team);

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
            Response response;

            int currentTrial = 1;

            // the coach is waiting for referee command
            clock.increment(this);
            response = bench.reviewNotes(clock, this.team, currentTrial, knockout);
            clock.update(response.getClock());

            currentState = CoachState.longName(response.getState());
            System.out.println(currentState);

            //clocks = response.getClocks();
            boolean hasMoreOper;
            do {

                System.out.println(currentState);

                switch (currentState) {

                    case WAIT_FOR_REFEREE_COMMAND:
                        try {
                            // wait for referee to call this coach

                            clock.increment(this);
                            response = bench.waitForCoachCall(clock);
                            clock.update(response.getClock());

                            // call contestants
                            clock.increment(this);
                            response = bench.callContestants(clock, this.team, this.getStrategy().shortName());
                            clock.update(response.getClock());

                            currentState = CoachState.longName(response.getState());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;


                    case ASSEMBLE_TEAM:
                        try {

                            // the coach is moved to the playground
                            clock.increment(this);
                            response = playground.moveCoachToPlayground(clock, this.team);
                            clock.update(response.getClock());

                            int iShouldInformRef = response.getIntVal();

                            // the last of the coaches informs the referee
                            // this variable is always set in the operation moveCoachToPlayground
                            if (iShouldInformRef == this.team) {
                                clock.increment(this);
                                response = refSite.informReferee(clock, this.team);
                                clock.update(response.getClock());

                                changeState(CoachState.longName(response.getState()));
                            }
                            else {
                                changeState(CoachState.longName(response.getState()));
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;


                    case WATCH_TRIAL:
                        try {
                            clock.increment(this);
                            response = playground.reviewNotes(clock, this.team);
                            clock.update(response.getClock());
                            int result = response.getIntVal();
                            knockout = response.isBoolVal();
                            currentTrial = response.getInt2Val();

                            changeStrategy(result);

                            // update this team's contestants' strength
                            clock.increment(this);
                            response = bench.reviewNotes(clock, this.team, currentTrial + 1, knockout);
                            clock.update(response.getClock());

                            //clocks = response.getClocks();
                            currentState = CoachState.longName(response.getState());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }


                response = bench.coachesHaveMoreOperations();
                hasMoreOper = response.isBoolVal();
            } while(hasMoreOper);

            System.out.println("O treinador da equipa "+ team + " terminou.");
            System.out.println(clock);
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
