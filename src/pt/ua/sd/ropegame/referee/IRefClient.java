package pt.ua.sd.ropegame.referee;

/**
 * Created by rui on 4/27/16.
 */
public interface IRefClient {

    void start();
    void join() throws InterruptedException;
}
