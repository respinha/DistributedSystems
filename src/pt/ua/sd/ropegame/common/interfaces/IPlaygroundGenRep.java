package pt.ua.sd.ropegame.common.interfaces;

public interface IPlaygroundGenRep extends IStatesGenRep {


    void updateContestantPosition(int contestantID, int teamID, int position);
    void updateTrial(int trial);
    void updateRopePosition(int ropePos);
}
