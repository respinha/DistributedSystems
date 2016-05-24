package pt.ua.sd.ropegame.common.enums;

import pt.ua.sd.ropegame.team.Coach;

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

