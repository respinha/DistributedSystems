package pt.ua.sd.ropegame.enums;

/**
 * Contestants' states.
 */
public enum ContestantState {
    STAND_IN_POSITION("SIP"), SEAT_AT_THE_BENCH("SAB"), DO_YOUR_BEST("DYB");


    private String shortName;

    ContestantState(String s) {
        this.shortName = s;
    }

    public String shortName() {
        return this.shortName;
    }

}
