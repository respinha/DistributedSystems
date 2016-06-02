package pt.ua.sd.ropegame.common.communication;

/**
 * Message used to communicate with {@link pt.ua.sd.ropegame.refereesite.RefereeSiteServer}
 * */
public class MessageToRefSite extends Message {
    private int team;

    private int currentTrial, ropePos;
    private boolean knockout;
    /**
     * Constructor for a generic message
     *
     * @param msgType  Message type.
     * @param senderID ID of the entity that created this message.
     */
    public MessageToRefSite(int msgType, int senderID) {
        super(msgType, senderID);
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param senderID
     * @param teamID
     */
    public MessageToRefSite(int msgType, int senderID, int teamID) {
        super(msgType, senderID);

        this.team = teamID;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param currentTrial
     * @param knockout
     */
    public MessageToRefSite(int msgType, int sender, int currentTrial, boolean knockout) {
        super(msgType, sender);
        this.currentTrial = currentTrial;
        this.knockout = knockout;
    }

    /**
     * Constructor with relevant parameters for messaging.
     * @param msgType
     * @param sender
     * @param currentTrial
     * @param ropePos
     * @param knockout
     */
    public MessageToRefSite(int msgType, int sender, int currentTrial, int ropePos, boolean knockout) {
        super(msgType, sender);
        this.currentTrial = currentTrial;
        this.ropePos = ropePos;
        this.knockout = knockout;
    }

    public int getCurrentTrial() {
        return currentTrial;
    }

    public boolean isKnockout() {
        return knockout;
    }

    public int getRopePos() {
        return ropePos;
    }

    public int getTeamID() {
        return team;
    }

    public int getGameNumber() { return team; }
}
