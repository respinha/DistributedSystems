package pt.ua.sd.ropegame.common.interfaces;

public interface IRefSiteGenRep extends IStatesGenRep {

    void updateGame(int game);
    void updateMatchWinner(int winner, int[] results);
    void updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout);
    void generateLogFile();

}
