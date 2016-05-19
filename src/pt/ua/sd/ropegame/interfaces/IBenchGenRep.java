package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.memregions.Bench;

public interface IBenchGenRep extends IStatesGenRep {


    void removeContestantFromPosition(int team, int pos);

    void updateStrengths(int teamID, int[] strength);
}
