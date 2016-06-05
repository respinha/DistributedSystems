package pt.ua.sd.ropegame.common.interfaces;

/**
 * An Interface every class waiting for a clock change must implement.
 */
public interface IClockChangeListener {
    void clockUpdated();
}
