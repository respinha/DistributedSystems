package pt.ua.sd.ropegame.common.communication;

/**
 * Reply message. It may contain relevant fields in order to allow clients to extract info from servers.
 */
public class MessageReply extends Message {

    private boolean booleanResponse;
    private int intResponse;
    private String stringResponse;
    private int[] time;

    /**
     * Constructor for a generic message
     *
     * @param msgType  Message type.
     * @param senderID ID of the entity that created this message.
     */
    public MessageReply(int msgType, int senderID) {
        super(msgType, senderID);
    }

    public MessageReply(int msgType, int sender, boolean booleanResponse) {
        super(msgType, sender);
        this.booleanResponse = booleanResponse;
    }

    public MessageReply(int msgType, int sender, int intResponse) {
        super(msgType, sender);
        this.intResponse = intResponse;
    }

    public MessageReply(int msgType, int sender, String stringResponse) {
        super(msgType, sender);
        this.stringResponse = stringResponse;
    }

    public MessageReply(int msgType, int sender, int intResponse, String stringResponse) {
        super(msgType, sender);
        this.intResponse = intResponse;
        this.stringResponse = stringResponse;
    }

    public MessageReply(int message, int sender, boolean booleanResponse, String stringResponse) {
        super(message, sender);
        this.booleanResponse = booleanResponse;
        this.stringResponse = stringResponse;
    }

    public boolean getBooleanResponse() {
        return booleanResponse;
    }

    public int getIntResponse() {
        return intResponse;
    }

    public String getStringResponse() { return stringResponse; }

    public int[] getTime() {
        return time;
    }
}
