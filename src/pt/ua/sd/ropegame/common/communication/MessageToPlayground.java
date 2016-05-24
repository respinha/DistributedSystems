package pt.ua.sd.ropegame.common.communication;

/**
 * Message used to communicate with {@link pt.ua.sd.ropegame.playground.PlaygroundServer}
 * */
public class MessageToPlayground extends Message {
    private int strength;
    private int memberID;
    private int team;

    /**
     * Constructor for a generic message
     *
     * @param msgType  Message type.
     * @param senderID ID of the entity that created this message.
     */
    public MessageToPlayground(int msgType, int senderID) {
        super(msgType, senderID);
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param senderID
     * @param teamID
     */
    public MessageToPlayground(int msgType, int senderID, int teamID) {
        super(msgType, senderID);
        this.team = teamID;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param senderID
     * @param teamID
     * @param gameMemberID
     * @param strength
     */
    public MessageToPlayground(int msgType, int senderID, int teamID, int gameMemberID, int strength) {
        super(msgType, senderID);
        this.team = teamID;
        this.memberID = gameMemberID;
        this.strength = strength;
    }

    public int getTeam() {
        return team;
    }

    public int getStrength() {
        return strength;
    }

    public int getGameMemberID() {
        return memberID;
    }

    public int getTeamID() {
        return team;
    }
}
