package pt.ua.sd.ropegame.interfaces;


import pt.ua.sd.ropegame.entities.Referee;

public interface IRefBench {

    void callTrial(Referee referee) throws InterruptedException;

    void notifyContestantsMatchIsOver();
}
