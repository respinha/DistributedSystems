package pt.ua.sd.ropegame.interfaces;

import pt.ua.sd.ropegame.entities.Coach;

public interface ICoachBench {

    void reviewNotes(Coach coach, int teamID, int trial, boolean knockout);

    void callContestants(Coach coach, int teamID, String strategy) throws InterruptedException;

    void waitForCoachCall() throws InterruptedException;

    boolean coachesHaveMoreOperations();

}
