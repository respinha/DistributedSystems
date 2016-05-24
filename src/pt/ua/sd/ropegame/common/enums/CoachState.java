package pt.ua.sd.ropegame.common.enums;

/**
 * Coach states.
 */
public enum CoachState {
    WATCH_TRIAL("WATR"), ASSEMBLE_TEAM("ASTE"), WAIT_FOR_REFEREE_COMMAND("WFRC");

    private String shortName;

    CoachState(String s) {
        this.shortName = s;
    }

    public String shortName() {
        return this.shortName;
    }

    public static CoachState longName(String shortName) {
        for(CoachState e : CoachState.values())
            if(shortName.equals(e.shortName()))
                return e;

        return null;
    }

}
