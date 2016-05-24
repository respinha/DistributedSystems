package pt.ua.sd.ropegame.common.interfaces;

public interface IStatesGenRep {

    void updateCoachState(String state, int teamID);

    void updateContestantState(String state, int gameMemberID, int teamID);

    void updateRefState(String state);

    boolean requestToDie();

}
