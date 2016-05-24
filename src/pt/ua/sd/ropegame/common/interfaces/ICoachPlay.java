package pt.ua.sd.ropegame.common.interfaces;


public interface ICoachPlay {

    int getCurrentTrial();

    int reviewNotes(int teamID) throws InterruptedException;

    int moveCoachToPlayground(int teamID) throws InterruptedException;

    boolean isKnockout();

    boolean closePlaygroundConnection();
}
