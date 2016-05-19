package pt.ua.sd.ropegame.enums;

/**
 * Coach states.
 */
public enum CoachState {
    WATCH_TRIAL("WATR  "), ASSEMBLE_TEAM("ASTE  "), WAIT_FOR_REFEREE_COMMAND("WFRC  ");

    private String shortName;

    CoachState(String s) {
        this.shortName = s;
    }

    public String shortName() {
        return this.shortName;
    }

}
