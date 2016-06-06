package pt.ua.sd.ropegame.common.enums;

/**
 * Contestants' states.
 */
public enum ContestantState {
    STAND_IN_POSITION("SIP"), SEAT_AT_THE_BENCH("SAB"), DO_YOUR_BEST("DYB");


    private String shortName;

    /**
     * Constructor for a contestant's state.
     * @param s state's shortname.
     */
    ContestantState(String s) {
        this.shortName = s;
    }

    /**
     * Get state's shortname.
     * @return state's shortname.
     */
    public String shortName() {
        return this.shortName;
    }

    /**
     * Convert shortname to longname.
     * @param shortName state's shortname.
     * @return state's long name.
     */
    public static ContestantState longName(String shortName) {
        for(ContestantState e : ContestantState.values())
            if(shortName.equals(e.shortName()))
                return e;

        return null;
    }

}
