package pt.ua.sd.ropegame.common.interfaces;

import java.rmi.Remote;

public interface IBenchGenRep extends IStatesGenRep {

    void removeContestantFromPosition(int team, int pos);

    void updateStrengths(int teamID, int[] strength);
}
