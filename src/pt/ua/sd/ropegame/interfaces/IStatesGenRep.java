package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.enums.CoachState;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.enums.RefereeState;

/**
 * Created by rui on 3/30/16.
 */
public interface IStatesGenRep {

    void updateCoachState(String state, int teamID);
    void updateContestantState(String state, int gameMemberID, int teamID);

    void updateRefState(String state);

}
