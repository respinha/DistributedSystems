package pt.ua.sd.ropegame.genrepository;


import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.VectClock;
import pt.ua.sd.ropegame.common.interfaces.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * General repository: generates the log file.
 */
class GeneralRepository implements
        IBenchGenRep, IRefSiteGenRep, IPlaygroundGenRep, IClockChangeListener {


    private Lock mutex;
    private String[] currentStatus;
    private int deadRegions;
    private VectClock vectClock;

    @Override
    public void clockUpdated() {
        for(int i = 0; i < vectClock.getClocks().length; i++) {
            currentStatus[STATUSID.REFCLK.id + i] = vectClock.getClocks()[i] + "";
        }
    }
    // positions to write data to.

    private enum STATUSID {
        REFSTAT(0),
        COACH1STAT(1), CONT01STAT(2), CONT02STAT(4), CONT03STAT(6), CONT04STAT(8), CONT05STAT(10),
        CONT01SG(3), CONT02SG(5), CONT03SG(7), CONT04SG(9), CONT05SG(11),
        COACH2STAT(12), CONT11STAT(13), CONT12STAT(15), CONT13STAT(17), CONT14STAT(19), CONT15STAT(21),
        CONT11SG(14), CONT12SG(16), CONT13SG(18), CONT14SG(20), CONT15SG(22),
        TEAM03(23), TEAM02(24), TEAM01(25), ROPE(26), TEAM11(27), TEAM12(28), TEAM13(29), NB(30), PS(31),
        REFCLK(32),
        COACH1CLK(33), CONT01CLK(34),CONT02CLK(35), CONT03CLK(36), CONT04CLK(37),CONT05CLK(38),
        COACH2CLK(39), CONT11CLK(40),CONT12CLK(41), CONT13CLK(42),CONT14CLK(43), CONT15CLK(44);

        private int id;

        STATUSID(int numVal) {
            this.id = numVal;
        }

    }

    // variables needed to create and write to a text file
    private Path file;
    private ArrayList<String> lines;
    private boolean write = false;

    /**
     * Constructor for the general repository.
     * @param configs
     */
    GeneralRepository(GameOfTheRopeConfigs configs) {
        mutex = new ReentrantLock();

        currentStatus = new String[45];

        for (int i = 0; i < currentStatus.length; i++) {
            currentStatus[i] = "-";
        }

        currentStatus[STATUSID.ROPE.id] = ".";

        // create log file
        file = Paths.get(configs.getLogFileName());

        //logger = new Logger(32, 2);
        lines = new ArrayList<>();

        deadRegions = 0;

        vectClock = new VectClock(configs);
        vectClock.assignClockListener(this);
        printFirstLines();
    }


    /**
     * Print table titles.
     */
    private void printFirstLines() {

        // title
        String row[] = new String [1];
        int[] size = new int[1];

        row[0] = "Game of the Rope - Description of the internal state";
        size[0] = 45;

        String line = Logger.log(row, size);
        lines.add(line);

        System.out.println(line);

        // second row

        row = new String[] {
                "Ref",
                "Coa 1", "Cont 1", "Cont 2", "Cont 3", "Cont 4", "Cont 5",
                "Coa 2", "Cont 1", "Cont 2", "Cont 3", "Cont 4", "Cont 5",
                "   Trial", "", "", "", "", "", "", "", "",
                "", "", "", "", "","", "", "", "", "", "VCk",
                "", "", "", "", "", "", "", "", "", "", "", "", ""
        };

        size = new int[] {
                4,
                5, 6, 6, 6, 6, 6,
                5, 6, 6, 6, 6, 6,
                9, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0,
                5,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };

        line = Logger.log(row, size);
        lines.add(line);

        System.out.println(line);

        // third row

        row = new String[] {
                "Stat",
                "Stat", "Sta","SG", "Sta","SG", "Sta","SG", "Sta","SG", "Sta","SG",
                "Stat", "Sta","SG", "Sta","SG", "Sta","SG", "Sta","SG", "Sta","SG",
                "3", "2", "1", ".", "1", "2", "3", "NB", "PS",
                " 0", " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", " 9", "10", "11", "12"

        };

        size = new int[] {
                4,
                5, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
                5, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
                1, 1, 1, 1, 1, 1, 1,
                2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
        };

        line = Logger.log(row, size);
        lines.add(line);

        System.out.println(line);

    }

    @Override
    public void updateGame(VectClock clientClock, int game) {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            String s1 = "Game " + game;
            lines.add(s1);
            System.out.println(s1);

        } finally {
            mutex.unlock();
        }
    }

    /**
     * Displays "Match was won by team # (#-#). / was a draw." message.
     * @param winner team which won the match.
     * @param results final results.
     */
    @Override
    public void updateMatchWinner(VectClock clientClock, int winner, int[] results) {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            String s1;

            if (results[0] == results[1])
                s1 = "Match was a draw.";

            else {
                s1 = "Match was won by team " + winner + " ";
                s1 += "(" + results[0] + " - " + results[1] + ").";
            }

            lines.add(s1);
            System.out.println(s1);

            generateLogFile();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Displays "Game # was won by team # by knock out in # trials. / by points. / was a draw." message.
     * @param currentGame current game.
     * @param gameWinner game winner.
     * @param ntrials current trial when game ended.
     * @param knockout true if one of the teams won by knockout.
     */
    @Override
    public void updateGameWinner(VectClock clientClock, int currentGame, int gameWinner, int ntrials, boolean knockout) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            String s1 = "Game " + currentGame + " ";
            if (gameWinner == 0)
                s1 += "was a draw.";
            else {
                s1 += "was won by team ";
                if (knockout)
                    s1 += gameWinner + " by knockout in " + ntrials + " trials.";

                else
                    s1 += gameWinner + " by points.";
            }


            lines.add(s1);
            System.out.println(s1);

        } finally {
            mutex.unlock();
        }
    }

    /** Update referee state.
     * @param state Referee's new State.
     */
    @Override
    public void updateRefState(VectClock clientClock, String state) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            if (!write) // && currentStatus[STATUSID.PS.id] != "-")
                write = true;
            currentStatus[STATUSID.REFSTAT.id] = state;
            printStatus();
        } finally {
            mutex.unlock();
        }

    }

    @Override
    public void requestToDie() {
        mutex.lock();
         try {
             deadRegions++;
             if(deadRegions == 3) {
                 System.out.println(vectClock);
             }

         } finally {
             mutex.unlock();
         }
    }


    /**
     * Print current status.
     */
    private void printStatus() {

        if(!write) return;

        ArrayList<String> row = new ArrayList<>();

        int[] size = new int[] {
                4,
                5, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
                5, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
                1, 1, 1, 1, 1, 1, 1,
                2, 2,
                2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
        };


        Collections.addAll(row, currentStatus);


        String line = Logger.log(row.toArray(new String[0]), size);
        lines.add(line);
        System.out.println(line);
    }


    /**
     * Update contestant state.
     * @param state new contestant state.
     * @param gameMemberID The contestant's ID.
     */
    @Override
    public void updateContestantState(VectClock clientClock, String state, int gameMemberID, int teamID) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            updateStates(teamID, gameMemberID, state);

            printStatus();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update all contestants strengths.
     * @param teamID
     * @param strength
     */
    @Override
    public void updateStrengths(VectClock clientClock, int teamID, int[] strength) {

        mutex.lock();

        try {
            vectClock.update(clientClock);
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

        } finally {
            mutex.unlock();
        }

    }

    @Override
    public void updateClock(VectClock clientClock) {
       mutex.lock();

        vectClock.update(clientClock);

        mutex.unlock();
    }

    /**
     * Update a single contestant state.
     * @param teamID
     * @param gameMemberID
     * @param state
     */
    private void updateStates(int teamID, int gameMemberID, String state) {
        if(teamID == 0) {
            switch (gameMemberID) {
                case 0:
                    currentStatus[STATUSID.CONT01STAT.id] = state;
                    break;
                case 1:
                    currentStatus[STATUSID.CONT02STAT.id] = state;
                    break;
                case 2:
                    currentStatus[STATUSID.CONT03STAT.id] = state;
                    break;
                case 3:
                    currentStatus[STATUSID.CONT04STAT.id] = state;
                    break;
                case 4:
                    currentStatus[STATUSID.CONT05STAT.id] = state;
                    break;
            }


        }
        else {
            switch (gameMemberID) {
                case 0:
                    currentStatus[STATUSID.CONT11STAT.id] = state;
                    break;
                case 1:
                    currentStatus[STATUSID.CONT12STAT.id] = state;
                    break;
                case 2:
                    currentStatus[STATUSID.CONT13STAT.id] = state;
                    break;
                case 3:
                    currentStatus[STATUSID.CONT14STAT.id] = state;
                    break;
                case 4:
                    currentStatus[STATUSID.CONT15STAT.id] = state;
                    break;
            }

        }
    }

    /**
     * Update Coach State
     * @param state new coach state.
     */
    @Override
    public void updateCoachState(VectClock clientClock, String state, int teamID) {
        mutex.lock();


        try {
            vectClock.update(clientClock);
            if (teamID == 0)
                currentStatus[STATUSID.COACH1STAT.id] = state;
            else
                currentStatus[STATUSID.COACH2STAT.id] = state;

            printStatus();

        }
        finally {
            mutex.unlock();
        }

    }

    /**
     * Remove a contestant from a playground position.
     * @param team Contestant's team
     * @param pos Contestant's posistion in playground,
     */
    @Override
    public void removeContestantFromPosition(VectClock clientClock, int team, int pos) {

        mutex.lock();

        try {
            vectClock.update(clientClock);
            switch (pos) {
                case 1:
                    if (team == 0) currentStatus[STATUSID.TEAM03.id] = "-";
                    else currentStatus[STATUSID.TEAM11.id] = "-";
                    break;

                case 2:
                    if (team == 0) currentStatus[STATUSID.TEAM02.id] = "-";
                    else currentStatus[STATUSID.TEAM12.id] = "-";
                    break;

                case 3:
                    if (team == 0) currentStatus[STATUSID.TEAM01.id] = "-";
                    else currentStatus[STATUSID.TEAM13.id] = "-";
                    break;

                default:
                    break;
            }

            printStatus();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update a contestant's position in playground.
     * @param id
     * @param teamID
     * @param pos
     */
    @Override
    public void updateContestantPosition(VectClock clientClock, int id, int teamID, int pos) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            switch (teamID) {
                case 0:
                    switch (pos) {
                        case 0: // no position
                            break;
                        case 1:
                            currentStatus[STATUSID.TEAM03.id] = String.valueOf(id + 1);
                            break;
                        case 2:
                            currentStatus[STATUSID.TEAM02.id] = String.valueOf(id + 1);
                            break;
                        case 3:
                            currentStatus[STATUSID.TEAM01.id] = String.valueOf(id + 1);
                            break;

                    }
                    break;
                case 1:
                    switch (pos) {
                        case 0:
                            break;
                        case 1:
                            currentStatus[STATUSID.TEAM11.id] = String.valueOf(id + 1);
                            break;
                        case 2:
                            currentStatus[STATUSID.TEAM12.id] = String.valueOf(id + 1);
                            break;
                        case 3:
                            currentStatus[STATUSID.TEAM13.id] = String.valueOf(id + 1);
                            break;
                    }
                    break;
            }

            printStatus();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update current trial number.
     * @param trial current trial number.
     */

    @Override
    public void updateTrial(VectClock clientClock, int trial) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            currentStatus[STATUSID.NB.id] = String.valueOf(trial);
            printStatus();

        } finally {
            mutex.unlock();
        }
    }

    /**
     * Update current rope position.
     * @param ropePos current rope position.
     */
    @Override
    public void updateRopePosition(VectClock clientClock, int ropePos) {
        mutex.lock();

        try {
            vectClock.update(clientClock);
            currentStatus[STATUSID.PS.id] = String.valueOf(ropePos);
            printStatus();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Generate final log file.
     */

    private void generateLogFile() {

        try {
            Files.write(file, lines, Charset.forName("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
