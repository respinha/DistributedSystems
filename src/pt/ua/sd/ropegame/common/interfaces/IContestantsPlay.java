package pt.ua.sd.ropegame.common.interfaces;


public interface IContestantsPlay extends ITeamPlayground {

    int standInLine(int gameMemberID, int teamID, int strength);
    void getReady(int gameMemberID, int teamID, int strength) throws InterruptedException;
    void pullTheRope() throws InterruptedException;
    boolean amDone() throws InterruptedException;

}
