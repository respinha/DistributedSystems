package pt.ua.sd.ropegame.common.communication;

import java.io.Serializable;

/**
 * Created by davidsilva on 03/06/16.
 */
public class Response implements Serializable {

    private int[] clocks;
    private String state;
    private boolean boolVal;
    private int intVal;
    private int int2Val;

    public Response(int[] clocks) {
        this.clocks = clocks;
    }

    public Response(int[] clocks, int intVal) {
        this.clocks = clocks;
        this.intVal = intVal;
    }

    public Response(int[] clocks, int intVal, boolean boolVal) {
        this.clocks = clocks;
        this.intVal = intVal;
        this.boolVal = boolVal;
    }

    public Response(int[] clocks, int intVal, int int2Val, boolean boolVal) {
        this.clocks = clocks;
        this.intVal = intVal;
        this.int2Val = int2Val;
        this.boolVal = boolVal;
    }

    public Response(int[] clocks, boolean boolVal) {
        this.clocks = clocks;
        this.boolVal = boolVal;
    }
    public Response(int[] clocks, String state) {
        this.clocks = clocks;
        this.state = state;
    }

    public Response(int[] clocks, String state, int intVal) {
        this.clocks = clocks;
        this.state = state;
        this.intVal = intVal;
    }

    public Response(int[] clocks, String state, boolean boolVal) {
        this.clocks = clocks;
        this.state = state;
        this.boolVal = boolVal;
    }

    public String getState() {
        return state;
    }

    public int[] getClocks() {
        return clocks;
    }

    public boolean isBoolVal() {
        return boolVal;
    }

    public int getIntVal() {
        return intVal;
    }

    public int getInt2Val() {
        return int2Val;
    }
}
