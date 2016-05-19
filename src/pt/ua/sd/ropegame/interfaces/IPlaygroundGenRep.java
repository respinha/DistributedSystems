package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.memregions.Playground;

public interface IPlaygroundGenRep extends IStatesGenRep {


    void updateContestantPosition(int gameMemberID, int teamID, int position);
    void updateTrial(int trial);
    void updateRopePosition(int ropePos);
}
