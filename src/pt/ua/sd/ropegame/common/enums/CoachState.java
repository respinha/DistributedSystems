package pt.ua.sd.ropegame.common.enums;

/**
 * Coach states.
 */
public enum CoachState {
    WATCH_TRIAL("WATR "), ASSEMBLE_TEAM("ASTE "), WAIT_FOR_REFEREE_COMMAND("WFRC ");

    private String shortName;

    /**
     * Constructor for a Coach State.
     * @param s Shortname.
     */
    CoachState(String s) {
        this.shortName = s;
    }

    /**
     * Get Coach State's shortname.
     * @return Coach State's shortname.
     */
    public String shortName() {
        return this.shortName;
    }

    /**
     * Convert a Coach State's shortname to its longName equivalent.
     * @param shortName Coach State's shortname.
     * @return Long Name.
     */
    public static CoachState longName(String shortName) {
        for(CoachState e : CoachState.values())
            if(shortName.equals(e.shortName()))
                return e;

        return null;
    }

}
