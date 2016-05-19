package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.memregions.RefereeSite;

public interface IRefSiteGenRep extends IStatesGenRep {

    void updateMatchWinner(int winner, int[] results);
    void updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout);
    void generateLogFile();


}
