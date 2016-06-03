package pt.ua.sd.ropegame.common.interfaces;

public interface IContestantsBench extends ITeamBench {

    int waitForContestantCall(int gameMemberID, int teamID) throws InterruptedException;

    void seatDown(int gameMemberID, int teamID, int strength, int position, boolean gameOver);

    boolean contestantsHaveMoreOperations();

    boolean closeBenchConnection();
}
