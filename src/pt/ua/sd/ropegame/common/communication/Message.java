package pt.ua.sd.ropegame.common.communication;

import java.io.Serializable;

/**
 * Generic Message. Each communication pair has its own {@link MessageType}.
 */
public abstract class Message implements Serializable {
    private int msgType;
    private int senderID;

    private final long serialID = 29042016L;
    /**
     * Constructor for a generic message
     * @param msgType Message type.
     * @param senderID ID of the entity that created this message.
     */
    public Message(int msgType, int senderID) {
        // TODO: Check for valid input

        this.msgType = msgType;
        this.senderID = senderID;
    }


    /**
     * @return Message type.
     */
    public int getMsgType() {
        return this.msgType;
    }

    /**
     * @return Creator Entity ID.
     */
    public int senderID() {
        return senderID;
    }
}
