package pt.ua.sd.ropegame.common.interfaces;


import java.rmi.Remote;

public interface IRefBench extends Remote {

    void callTrial();

    void notifyContestantsMatchIsOver();
}
