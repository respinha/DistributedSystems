package pt.ua.sd.ropegame.common.interfaces;


public interface ICoachPlay extends ITeamPlayground {

    int getCurrentTrial();

    int reviewNotes(int teamID) throws InterruptedException;

    int moveCoachToPlayground(int teamID) throws InterruptedException;

    boolean isKnockout();

    boolean closePlaygroundConnection();
}
