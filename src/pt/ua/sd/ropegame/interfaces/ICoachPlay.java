package pt.ua.sd.ropegame.interfaces;


import pt.ua.sd.ropegame.entities.Coach;

public interface ICoachPlay extends IPlayground {

    int getCurrentTrial();

    int reviewNotes(int teamID) throws InterruptedException;

    int moveCoachToPlayground(Coach coach, int teamID) throws InterruptedException;

    boolean isKnockout();
}
