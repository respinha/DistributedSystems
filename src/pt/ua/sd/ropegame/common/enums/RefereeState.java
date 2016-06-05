package pt.ua.sd.ropegame.common.enums;

/**
 * Referee's states.
 */

public enum RefereeState {
    START_OF_THE_MATCH("STM  "), START_OF_A_GAME("SOG  "), TEAMS_READY("TRE  "), WAIT_FOR_TRIAL_CONCLUSION("WTC  "), END_OF_A_GAME("EOG  "), END_OF_THE_MATCH("EOM  ");

    private String shortName;

    RefereeState(String s) {
        this.shortName = s;
    }

    public String shortName() {
        return this.shortName;
    }

    public static RefereeState longName(String shortName) {
        for(RefereeState e : RefereeState.values())
            if(shortName.equals(e.shortName()))
                return e;

        return null;
    }

}
