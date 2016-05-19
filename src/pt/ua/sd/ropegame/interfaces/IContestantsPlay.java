package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Contestant;


public interface IContestantsPlay extends IPlayground {

    int standInLine(Contestant contestant, int gameMemberID, int teamID, int strength) throws InterruptedException;
    void getReady(Contestant contestant, int gameMemberID, int teamID, int strength) throws InterruptedException;
    void pullTheRope() throws InterruptedException;
    boolean amDone() throws InterruptedException;
}
