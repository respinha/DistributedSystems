package pt.ua.sd.ropegame.common.communication;

/**
 * Message used to communicate with Bench.
 */
public class MessageToBench extends Message {

    private boolean gameOver;
    // Not all elements are used in a message.
    private int currentTrial;
    private int ropePos;
    private int team;
    private boolean knockout;
    private String strategy;
    private int memberID;

    private int strength;
    private int memberPos;

    /**
     * Constructor.
     * @param type
     * @param senderID
     */
    public MessageToBench(int type, int senderID) {
        super(type, senderID);
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param currentTrial
     * @param arg
     * @param knockout
     */
    public MessageToBench(int message, int sender, int currentTrial, int arg, boolean knockout) {
        super(message, sender);

        this.currentTrial = currentTrial;

        switch(sender) {
            case MessageParticipant.REFEREE:
                this.ropePos = arg;
                break;
            case MessageParticipant.COACH:
                this.team = arg;
                break;
        }

        this.knockout = knockout;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param teamID
     * @param strategy
     */
    public MessageToBench(int message, int sender, int teamID, String strategy) {
        super(message, sender);
        this.team = teamID;
        this.strategy = strategy;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param teamID
     * @param memberID
     */
    public MessageToBench(int message, int sender, int teamID, int memberID) {
        super(message, sender);
        this.team = teamID;
        this.memberID = memberID;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param message
     * @param sender
     * @param teamID
     * @param memberID
     * @param strength
     * @param position
     * @param gameOver
     */
    public MessageToBench(int message, int sender, int teamID, int memberID, int strength, int position, boolean gameOver) {
        super(message, sender);
        this.team = teamID;
        this.memberID = memberID;
        this.strength = strength;
        this.memberPos = position;
        this.gameOver = gameOver;
    }

    public int getCurrentTrial() {
        return currentTrial;
    }

    public int getRopePos() {
        return ropePos;
    }

    public int getTeamID() {
        return team;
    }

    public boolean isKnockout() {
        return knockout;
    }

    public String getStrategy() {
        return strategy;
    }

    public int getGameMemberID() {
        return memberID;
    }

    public int getStrength() {
        return strength;
    }

    public int getContestantPosition() {
        return memberPos;
    }

    public boolean gameIsOver() {
        return gameOver;
    }
}
