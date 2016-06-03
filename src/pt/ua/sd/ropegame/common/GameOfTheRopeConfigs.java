package pt.ua.sd.ropegame.common;

/**
 * Module that contains all needed specifications in the project's modules.
 */
public class GameOfTheRopeConfigs {
    private int maxtrials;
    private int maxgames;
    private int nteams;
    private int ncoaches;
    private int ncontestants;
    private int maxcontplayground;
    private String log;
    private String genrephost;
    private int genrepport;
    private String benchhost;
    private int benchport;
    private String playgroundhost;
    private int playgroundport;
    private String refsitehost;
    private int refsiteport;

    private int rmiPort;
    private String rmiHost;


    public GameOfTheRopeConfigs(DOMParser parser) {
        maxtrials = parser.getMaxTrials();
        maxgames = parser.getMaxGames();
        nteams = parser.getNTeams();
        ncoaches = parser.getNCoaches();
        ncontestants = parser.getNContestants();
        maxcontplayground = parser.getMaxContsPlayground();
        log = parser.getLogFileName();
        genrephost = parser.getGenRepHostname();
        genrepport = parser.getGenRepPort();
        benchhost = parser.getBenchHostname();
        benchport = parser.getBenchPort();
        playgroundhost = parser.getPlaygroundHostname();
        playgroundport = parser.getPlaygroundPort();
        refsitehost = parser.getRefSiteHostName();
        refsiteport = parser.getRefSitePort();

        rmiHost = parser.getRMIHost();
        rmiPort = parser.getRMIPort();

    }

    /**
     * @return Max trials.
     */
    public int getMaxTrials() {
        return maxtrials;
    }

    /**
     * @return Max games.
     */
    public int getMaxGames() {
        return maxgames;
    }

    /**
     * @return number of teams.
     */
    public int getNTeams() {
        return nteams;
    }

    /**
     * @return number of coaches.
     */
    public int getNCoaches() {
        return ncoaches;
    }

    /**
     * @return number of contestants.
     */
    public int getNContestants() {
        return ncontestants;
    }

    /**
     * @return Max contestants in playground.
     */
    public int getMaxContsPlayground() {
        return maxcontplayground;
    }

    /**
     * @return log file name.
     */
    public String getLogFileName() {
        return log;
    }


    /**
     * @return General Repository's hostname.
     */
    public String getGenRepHostname() {
        return genrephost;
    }

    /**
     * @return General Repository's port.
     */
    public int getGenRepPort() {
        return genrepport;
    }


    /**
     * @return Bench's hostname.
     */
    public String getBenchHostname() {
        return benchhost;
    }

    /**
     * @return Bench's port.
     */
    public int getBenchPort() {
        return benchport;
    }

    /**
     * @return Playground's hostname
     */
    public String getPlaygroundHostname() {
        return playgroundhost;
    }

    /**
     * @return Playground's port.
     */
    public int getPlaygroundPort() {
        return playgroundport;
    }

    /**
     * @return Referee Site's Hostname.
     */
    public String getRefSiteHostName() {
        return refsitehost;
    }

    /**
     * @return Referee Site's port.
     */
    public int getRefSitePort() {
        return refsiteport;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public String getRmiHost() {
        return rmiHost;
    }
}
