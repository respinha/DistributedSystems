package pt.ua.sd.ropegame.common.communication;

/**
 * Message used to communicate with {@link pt.ua.sd.ropegame.genrepository.GeneralRepository}
 */
public class MessageToGenRep extends Message {

    private int[] strengths = new int[10];
    private String state;
    private int gameMemberID;
    private int teamID, strength, contestantPosition, currentTrial, ropePos, game, winner;
    private boolean knockout;
    private int[] results;

    /**
     * Constructor.
     * @param message
     * @param sender
     */
    public MessageToGenRep(int message, int sender) {
        super(message, sender);
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param state
     */
    public MessageToGenRep(int message, int sender, String state) {
        super(message, sender);
        this.state = state;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param intVal
     */
    public MessageToGenRep(int message, int sender, int intVal) {
        super(message, sender);
        switch (message) {
            case MessageType.UPDATE_TRIAL:
                this.currentTrial = intVal;
                break;
            case MessageType.UPDATE_ROPE_POSITION:
                this.ropePos = intVal;
                break;
            case MessageType.UPDATE_GAME:
                this.game = intVal;
                break;
        }
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param team
     * @param pos
     */
    public MessageToGenRep(int msgType, int sender, int team, int pos) {
        super(msgType, sender);
        this.teamID = team;
        this.contestantPosition = pos;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param teamID
     * @param strengths
     */
    public MessageToGenRep(int msgType, int sender, int teamID, int[] strengths) {
        super(msgType, sender);

        switch(msgType) {
            case MessageType.UPDATE_MATCH_WINNER:
                this.winner = teamID;
                this.results = strengths;
                break;
            case MessageType.UPDATE_STRENGTHS:
                this.teamID = teamID;
                this.strengths = strengths;
                break;
        }

    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param teamID
     * @param state
     */
    public MessageToGenRep(int msgType, int sender, int teamID, String state) {
        super(msgType, sender);
        this.teamID = teamID;
        this.state = state;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param gameMemberID
     * @param teamID
     * @param state
     */
    public MessageToGenRep(int msgType, int sender, int gameMemberID, int teamID, String state) {
        super(msgType, sender);
        this.gameMemberID = gameMemberID;
        this.teamID = teamID;
        this.state = state;
    }


    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param gameMemberID
     * @param teamID
     * @param intVal
     */
    public MessageToGenRep(int msgType, int sender, int gameMemberID, int teamID, int intVal) {
        super(msgType, sender);
        this.gameMemberID = gameMemberID;
        this.teamID = teamID;
        switch (msgType) {
            case MessageType.UPDATE_CONT_POSITION:
                this.contestantPosition = intVal;
                break;

        }
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param currentGame
     * @param gameWinner
     * @param ntrials
     * @param knockout
     */
    public MessageToGenRep(int msgType, int sender, int currentGame, int gameWinner, int ntrials, boolean knockout ) {
        super(msgType, sender);
        this.game = currentGame;
        this.winner = gameWinner;
        this.currentTrial = ntrials;
        this.knockout = knockout;
    }



    public String getState() {
        return this.state;
    }

    public int getGameMemberID() {
        return gameMemberID;
    }

    public int getTeamID() {
        return teamID;
    }

    public int getStrength() {
        return strength;
    }

    public int getContestantPosition() {
        return contestantPosition;
    }

    public int getCurrentTrial() {
        return currentTrial;
    }

    public int getRopePos() {
        return ropePos;
    }

    public int getGame() {
        return game;
    }

    public int getWinner() {
        return winner;
    }

    public boolean isKnockout() {
        return knockout;
    }

    public int[] getResults() {
        return results;
    }

    public int[] getStrengths() {
        return strengths;
    }
}
