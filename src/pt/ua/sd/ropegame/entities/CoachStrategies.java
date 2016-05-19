package pt.ua.sd.ropegame.entities;

import java.util.Random;

/**
 * Every {@link Coach} has a {@link CoachStrategies} object which contains three {@link Strategy} attributes related to different game situations.
 */
public class CoachStrategies {

    /**
     * The different strategies a Coach can have during the game.
     */
    public enum Strategy {
        RANDOM("RANDOM"), PICK_FIRST_THREE_CONTESTANTS("PICK_FIRST_THREE"), PICK_LAST_THREE_CONTESTANTS("PICK_LAST_THREE");

        private String shortName;

        Strategy(String s) {
            this.shortName = s;
        }

        public String shortName() {
            return this.shortName;
        }
    }

    private Strategy currentStrategy;
    private Strategy ifWinsStrategy;
    private Strategy ifLosesStrategy;
    private Strategy ifDrawStrategy;

    /**
     * Constructor for a {@link CoachStrategies} object with three random Strategies for the game.
     */
    public CoachStrategies() {
        this.currentStrategy = generateCoachStrategy();

        /*// we have to assure that the first strategy is not KEEP_SAME_TEAM
        while(this.currentStrategy == Strategy.KEEP_SAME_TEAM) {
            this.currentStrategy = generateCoachStrategy();
        }*/

        this.ifWinsStrategy = generateCoachStrategy();
        this.ifLosesStrategy = generateCoachStrategy();
        this.ifDrawStrategy = generateCoachStrategy();
    }

    /**
     * Constructor for a {@link CoachStrategies} object with predefined strategies
     *
     * @param initialStrategy The first strategy the Coach follows
     * @param ifWinsStrategy Strategy the Coach follows if his team wins the game
     * @param ifLosesStrategy Strategy the Coach follows if his team loses the game
     * @param ifDrawStrategy Strategy the Coach follows if trial ends in a draw
     * @throws IllegalArgumentException Initial can not be KEEP_SAME_TEAM
     */
    public CoachStrategies(Strategy initialStrategy, Strategy ifWinsStrategy, Strategy ifLosesStrategy, Strategy ifDrawStrategy) throws IllegalArgumentException{

        // we have to assure that initial strategy is not KEEP_SAME_TEAM
        //if(initialStrategy == Strategy.KEEP_SAME_TEAM)
        //    throw new IllegalArgumentException("Initial strategy can not be KEPP_SAME_TEAM.");

        this.currentStrategy = initialStrategy;
        this.ifLosesStrategy = ifLosesStrategy;
        this.ifWinsStrategy = ifWinsStrategy;
        this.ifDrawStrategy = ifDrawStrategy;
    }

    /**
     * @return Current coach strategy.
     */
    public Strategy getStrategy() {
        return this.currentStrategy;
    }

    /**
     * Changes the current coach strategy due to trial end.
     * @param result 1 if team wins the trial, -1 if loses, 0 if draw
     */
    public void trialFinished(int result) {
        switch (result) {
            case 0:
                currentStrategy = ifDrawStrategy;
                break;
            case 1:
                currentStrategy = ifWinsStrategy;
                break;
            case -1:
                currentStrategy = ifLosesStrategy;
                break;
        }
    }

    /**
     * @return A random Strategy.
     */
    public static Strategy generateCoachStrategy() {
        int pick = new Random().nextInt(Strategy.values().length);
        return Strategy.values()[pick];
    }
}

