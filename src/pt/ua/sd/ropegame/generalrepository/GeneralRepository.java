package pt.ua.sd.ropegame.memregions;

import genclass.GenericIO;
import pt.ua.sd.ropegame.entities.Contestant;
import pt.ua.sd.ropegame.entities.Referee;
import pt.ua.sd.ropegame.enums.CoachState;
import pt.ua.sd.ropegame.enums.ContestantState;
import pt.ua.sd.ropegame.enums.RefereeState;
import pt.ua.sd.ropegame.interfaces.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * General repository: generates the log file.
 */
public class GeneralRepository implements IRefereeGenRep, IContestantGenRep,
        ICoachGenRep, IBenchGenRep, IRefSiteGenRep, IPlaygroundGenRep {

    // clients
    private Referee ref;

    // memory regions
    private Playground playground;
    private Bench bench;
    private RefereeSite refereeSite;


    private Lock mutex;
    private String[] currentStatus;

    // positions to write data to.

    private enum STATUSID {
        REFSTAT(0),
        COACH1STAT(1), CONT01STAT(2), CONT02STAT(4), CONT03STAT(6), CONT04STAT(8), CONT05STAT(10),
        CONT01SG(3), CONT02SG(5), CONT03SG(7), CONT04SG(9), CONT05SG(11),
        COACH2STAT(12), CONT11STAT(13), CONT12STAT(15), CONT13STAT(17), CONT14STAT(19), CONT15STAT(21),
        CONT11SG(14), CONT12SG(16), CONT13SG(18), CONT14SG(20), CONT15SG(22),
        TEAM03(23), TEAM02(24), TEAM01(25), ROPE(26), TEAM11(27), TEAM12(28), TEAM13(29), NB(30), PS(31);


        private int id;

        STATUSID(int numVal) {
            this.id = numVal;
        }

    }

    // variables needed to create and write to a text file
    private Path file;
    ArrayList<String> lines;

    /**
     * Constructor for the general repository.
     */
    public GeneralRepository() {
        mutex = new ReentrantLock();

        currentStatus = new String[32];

        for (int i = 0; i < currentStatus.length; i++) {
            currentStatus[i] = new String("-");
        }

        currentStatus[STATUSID.ROPE.id] = ".";

        // create log file
        file = Paths.get("log.txt");
        lines = new ArrayList<>();

        printFirstLines();
    }


    /**
     * Print table titles.
     */
    private void printFirstLines() {

        String start = "Game of the Rope - Description of the internal state";
        String s1 = "Ref Coa 1 Cont 1 Cont 2 Cont 3 Cont 4 Cont 5 Coa 2 Cont 1 Cont 2 Cont 3 Cont 4 Cont 5    Trial";
        String s2 = "Sta Stat  Sta SG Sta SG Sta SG Sta SG Sta SG Stat  Sta SG Sta SG Sta SG Sta SG Sta SG 3 2 1 . 1 2 3 NB PS";
        lines.add(start);
        lines.add(s1);
        lines.add(s2);
        GenericIO.writelnString(start);
        GenericIO.writelnString(s1);
        GenericIO.writelnString(s2);
    }

    /**
     * Displays "Match was won by team # (#-#). / was a draw." message.
     * @param winner team which won the match.
     * @param results final results.
     */
    @Override
    public void updateMatchWinner(int winner, int[] results) {

        mutex.lock();
        String s1;

        if(results[0] == results[1])
            s1 = "Match was a draw.";

        else {
            s1 = "Match was won by team " + winner + " ";
            s1 += "(" + results[0] + " - " + results[1] + ").";
        }

        lines.add(s1);
        GenericIO.writelnString(s1);
        mutex.unlock();
    }

    /**
     * Displays "Game # was won by team # by knock out in # trials. / by points. / was a draw." message.
     * @param currentGame current game.
     * @param gameWinner game winner.
     * @param ntrials current trial when game ended.
     * @param knockout true if one of the teams won by knockout.
     */
    @Override
    public void updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout) {
        mutex.lock();

        String s1 = "Game " + currentGame + " ";
        if(gameWinner == 0)
            s1 += "was a draw.";
        else {
            s1 += "was won by team ";
            if(knockout)
                s1 += gameWinner + " by knockout in " + ntrials + " trials.";

            else
                s1 += gameWinner + " by points.";
        }

        lines.add(s1);
        GenericIO.writelnString(s1);
        mutex.unlock();
    }

    /** Update referee state.
     * @param state Referee's new State.
     */
    @Override
    public void updateRefState(String state) {
        mutex.lock();

        currentStatus[STATUSID.REFSTAT.id] = state;
        printStatus();

        mutex.unlock();

    }



    /**
     * Print current status.
     */
    private  void printStatus() {
        StringBuilder sb = new StringBuilder();

        for(String s: currentStatus) {
            sb.append(s);
            sb.append(" ");
        }

        String toPrint = sb.toString().trim();
        lines.add(toPrint);
        GenericIO.writelnString(toPrint);
    }


    /**
     * Update contestant state.
     * @param state new contestant state.
     * @param gameMemberID
     */
    @Override
    public void updateContestantState(String state, int gameMemberID, int teamID) {
        mutex.lock();

        try {

            updateStates(teamID, gameMemberID, state);

            printStatus();
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public void updateStrengths(int teamID, int[] strength) {

        mutex.lock();

        switch (teamID) {
            case 0:
                currentStatus[STATUSID.CONT01SG.id] = strength[0] + "";
                currentStatus[STATUSID.CONT02SG.id] = strength[1] + "";
                currentStatus[STATUSID.CONT03SG.id] = strength[2] + "";
                currentStatus[STATUSID.CONT04SG.id] = strength[3] + "";
                currentStatus[STATUSID.CONT05SG.id] = strength[4] + "";
                break;
            case 1:
                currentStatus[STATUSID.CONT11SG.id] = strength[0] + "";
                currentStatus[STATUSID.CONT12SG.id] = strength[1] + "";
                currentStatus[STATUSID.CONT13SG.id] = strength[2] + "";
                currentStatus[STATUSID.CONT14SG.id] = strength[3] + "";
                currentStatus[STATUSID.CONT15SG.id] = strength[4] + "";
                break;
        }

        printStatus();

        mutex.unlock();

    }

    private void updateStates(int teamID, int gameMemberID, String state) {
        if(teamID == 0) {
            switch (gameMemberID) {
                case 0:
                    currentStatus[STATUSID.CONT01STAT.id] = state;
                    // currentStatus[STATUSID.CONT01SG.id] = strength + " ";
                    break;
                case 1:
                    currentStatus[STATUSID.CONT02STAT.id] = state;
                    // currentStatus[STATUSID.CONT02SG.id] = strength + " ";
                    break;
                case 2:
                    currentStatus[STATUSID.CONT03STAT.id] = state;
                    // currentStatus[STATUSID.CONT03SG.id] = strength + " ";
                    break;
                case 3:
                    currentStatus[STATUSID.CONT04STAT.id] = state;
                    // currentStatus[STATUSID.CONT04SG.id] = strength + " ";
                    break;
                case 4:
                    currentStatus[STATUSID.CONT05STAT.id] = state;
                    // currentStatus[STATUSID.CONT05SG.id] = strength + " ";
                    break;
            }


        }
        else {
            switch (gameMemberID) {
                case 0:
                    currentStatus[STATUSID.CONT11STAT.id] = state;
                    // currentStatus[STATUSID.CONT11SG.id] = strength + "";
                    break;
                case 1:
                    currentStatus[STATUSID.CONT12STAT.id] = state;
                    // currentStatus[STATUSID.CONT12SG.id] = strength + "";
                    break;
                case 2:
                    currentStatus[STATUSID.CONT13STAT.id] = state;
                    //currentStatus[STATUSID.CONT13SG.id] = strength + "";
                    break;
                case 3:
                    currentStatus[STATUSID.CONT14STAT.id] = state;
                    // currentStatus[STATUSID.CONT14SG.id] = strength + "";
                    break;
                case 4:
                    currentStatus[STATUSID.CONT15STAT.id] = state;
                    // currentStatus[STATUSID.CONT15SG.id] = strength + "";
                    break;
            }

        }
    }

    /**
     * Update Coach State
     * @param state new coach state.
     */
    @Override
    public void updateCoachState(String state, int teamID) {
        mutex.lock();


        if(teamID == 0)
            currentStatus[STATUSID.COACH1STAT.id] = state;
        else
            currentStatus[STATUSID.COACH2STAT.id] = state;

        printStatus();

        mutex.unlock();

    }

    /**
     * Remove a contestant from a playground position. (unused)
     * @param team Contestant's team
     */
    @Override
    public void removeContestantFromPosition(int team, int pos) {

        mutex.lock();

        switch (pos) {
            case 1:
                if(team == 0) currentStatus[STATUSID.TEAM03.id] = "-";
                else currentStatus[STATUSID.TEAM11.id] = "-";
                break;

            case 2:
                if(team == 0) currentStatus[STATUSID.TEAM02.id] = "-";
                else currentStatus[STATUSID.TEAM12.id] = "-";
                break;

            case 3:
                if(team == 0) currentStatus[STATUSID.TEAM01.id] = "-";
                else currentStatus[STATUSID.TEAM13.id] = "-";
                break;

            default: break;
        }

        printStatus();

        mutex.unlock();
    }

    /**
     * Move a contestant to playground.
     */
    @Override
    public void updateContestantPosition(int id, int teamID, int pos) {
        mutex.lock();
        switch(teamID) {
            case 0:
                switch (pos) {
                    case 0: // no position
                        break;
                    case 1: currentStatus[STATUSID.TEAM03.id] = "   " + String.valueOf(id+1);
                        break;
                    case 2:currentStatus[STATUSID.TEAM02.id] = String.valueOf(id+1);
                        break;
                    case 3:currentStatus[STATUSID.TEAM01.id] = String.valueOf(id+1);
                        break;

                }
                break;
            case 1:
                switch(pos) {
                    case 0:
                        break;
                    case 1:currentStatus[STATUSID.TEAM11.id] = String.valueOf(id+1);
                        break;
                    case 2:currentStatus[STATUSID.TEAM12.id] = String.valueOf(id+1);
                        break;
                    case 3:currentStatus[STATUSID.TEAM13.id] = String.valueOf(id+1);
                        break;
                }
                break;
        }

        printStatus();

        mutex.unlock();
    }

    /**
     * Update current trial number.
     * @param trial current trial number.
     */

    @Override
    public void updateTrial(int trial) {
        mutex.lock();

        currentStatus[STATUSID.NB.id] = String.valueOf(trial);
        printStatus();

        mutex.unlock();
    }

    /**
     * Update current rope position.
     * @param ropePos current rope position.
     */
    @Override
    public void updateRopePosition(int ropePos) {
        mutex.lock();

        currentStatus[STATUSID.PS.id] = " " + String.valueOf(ropePos);
        printStatus();

        mutex.unlock();
    }

    /**
     * Generate final log file.
     */
    @Override
    public void generateLogFile() {
        mutex.lock();

        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mutex.unlock();
    }


}

