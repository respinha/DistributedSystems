package pt.ua.sd.ropegame.common.interfaces;

public interface ICoachBench {

    void reviewNotes(int teamID, int trial, boolean knockout);

    void callContestants(int teamID, String strategy);

    void waitForCoachCall() throws InterruptedException;

    boolean coachesHaveMoreOperations();
}
