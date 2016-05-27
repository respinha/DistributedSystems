package pt.ua.sd.ropegame.common.interfaces;

/**
 * Interface which implements all methods to use a vectorial clock in order to synchronize the current time in the distributed system.
 */
public interface IVectClock {

    void updateCurrentTime(int[] vectClocks);
    void sendCurrentTime();
}
