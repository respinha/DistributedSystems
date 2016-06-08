package pt.ua.sd.ropegame.common.communication;

import pt.ua.sd.ropegame.common.VectClock;
import java.io.Serializable;

/**
 * A class used to send a response to a remote function call.
 */
public class Response implements Serializable {

    private boolean boolVal2;
    private VectClock clock;
    private String state;
    private boolean boolVal;
    private int intVal;
    private int int2Val;

    public Response(VectClock clocks) {
        this.clock = clocks;
    }


    public Response(VectClock clock, int intVal) {
        this.clock = clock;
        this.intVal = intVal;
    }

    public Response(VectClock clock, int intVal, boolean boolVal) {
        this.clock = clock;
        this.intVal = intVal;
        this.boolVal = boolVal;
    }

    public Response(VectClock clock, String state, int intVal, boolean boolVal) {
        this.clock = clock;
        this.state = state;
        this.intVal = intVal;
        this.boolVal = boolVal;
    }

    public Response(VectClock clock, String state, int intVal, boolean boolVal, boolean boolVal2) {
        this.clock = clock;
        this.state = state;
        this.intVal = intVal;
        this.boolVal = boolVal;
        this.boolVal2 = boolVal2;
    }

    public Response(VectClock clock, int intVal, int int2Val, boolean boolVal) {
        this.clock = clock;
        this.intVal = intVal;
        this.int2Val = int2Val;
        this.boolVal = boolVal;
    }

    public Response(VectClock clock, boolean boolVal) {
        this.clock = clock;
        this.boolVal = boolVal;
    }
    public Response(VectClock clock, String state) {
        this.clock = clock;
        this.state = state;
    }

    public Response(VectClock clock, String state, int intVal) {
        this.clock = clock;
        this.state = state;
        this.intVal = intVal;
    }

    public Response(VectClock clock, String state, boolean boolVal) {
        this.clock = clock;
        this.state = state;
        this.boolVal = boolVal;
    }

    public Response(boolean boolVal) {
        this.boolVal = boolVal;
    }

    public String getState() {
        return state;
    }

    public VectClock getClock() {
        return clock;
    }

    public boolean getBoolVal() {
        return boolVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public int getInt2Val() {
        return int2Val;
    }

    public boolean isBoolVal2() {
        return boolVal2;
    }
}
