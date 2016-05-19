package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Contestant;

/**
 * Created by rui on 3/17/16.
 */
public interface IContestantsBench {

    int waitForContestantCall(int gameMemberID, int teamID) throws InterruptedException;

    void seatDown(Contestant contestant, int gameMemberID, int teamID, int strength, int position);

    boolean contestantsHaveMoreOperations();
}
