package pt.ua.sd.ropegame.enums;

/**
 * Referee's states.
 */

public enum RefereeState {
    START_OF_THE_MATCH("STM"), START_OF_A_GAME("SOG"), TEAMS_READY("TRE"), WAIT_FOR_TRIAL_CONCLUSION("WTC"), END_OF_A_GAME("EOG"), END_OF_THE_MATCH("EOM");

    private String shortName;

    RefereeState(String s) {
        this.shortName = s;
    }

    public String shortName() {
        return this.shortName;
    }

}
