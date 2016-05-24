package pt.ua.sd.ropegame.referee;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.interfaces.IRefSite;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.IRefBench;
import pt.ua.sd.ropegame.common.interfaces.IRefPlay;

/**
 *  RefClient is the client that establishes connection between this referee machine and the remote hosts.
 */
public class RefClient implements IRefBench, IRefPlay, IRefSite, IRefClient {

    private Referee referee;
    private GameOfTheRopeConfigs configs;

    /**
     * Constructor for a Referee Client
     * @param configs Game of the Rope configurations.
     */
    public RefClient(GameOfTheRopeConfigs configs) {
        referee = new Referee(this, this, this);
        this.configs = configs;
    }

    /**
     * Start referee thread.
     */
    public void start() {

        referee.start();
    }

    /**
     * Join referee thread.
     * @throws InterruptedException The thread was interrupted.
     */
    public void join() throws InterruptedException {

        referee.join();
    }

    /**
     * Get rope position.
     * @return Rope position.
     */
    @Override
    public int getRopePos() {

        ClientCom com = initCom(MessageParticipant.PLAYGROUND);
        MessageToPlayground outMessage = new MessageToPlayground(MessageType.GET_ROPE_POS, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.ROPE_POS)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return inMessage.getIntResponse();
    }

    /**
     * Call trial. (see documentation on the server side for more information).
     */
    @Override
    public void callTrial() {

        ClientCom com = initCom(MessageParticipant.BENCH);


        MessageToBench outMessage = new MessageToBench(MessageType.CALL_TRIAL, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_CALLED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));
    }

    /**
     * Notify contestants the match is over. (see documentation on the server side for more information).
     */
    @Override
    public void notifyContestantsMatchIsOver() {

        ClientCom com = initCom(MessageParticipant.BENCH);

        MessageToBench outMessage = new MessageToBench(MessageType.NOTIFY_CONTESTANTS, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.MATCH_OVER_CONTESTANTS)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Signal Referee Site a new trial is starting.
     */
    @Override
    public void startTrialRefSite() {

        ClientCom com = initCom(MessageParticipant.REFSITE);

        MessageToRefSite outMessage = new MessageToRefSite(MessageType.START_TRIAL_REF, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_STARTED_REFSITE)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));

    }

    /**
     * Make a decision based on the trial's current state.
     * @param currentTrial Current trial.
     * @param knockout Game was won by knockout.
     * @return !endOfGame False if game has more trials; true otherwise.
     */
    @Override
    public boolean assertTrialDecisionRefSite(int currentTrial, boolean knockout)
    {
        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.ASSERT_TRIAL_DECISION_REF, MessageParticipant.REFEREE, currentTrial, knockout);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_DECIDED_REFSITE)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        if(inMessage.getBooleanResponse())
            referee.changeState(RefereeState.longName(inMessage.getStringResponse()));

        return inMessage.getBooleanResponse();
    }

    /**
     * Start a new Match on Referee Site.
     */
    @Override
    public void startTheMatch() {

        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.START_MATCH, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.MATCH_STARTED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));
    }

    /**
     * Check if referee has more operations.
     * @return True if Referee has more operations; false otherwise.
     */
    @Override
    public boolean refHasMoreOperations() {

        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.REF_HAS_MORE_OPER, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REPLY_REF_HAS_MORE_OPER)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return inMessage.getBooleanResponse();
    }

    /**
     * Message RefereeSite to close connection.
     */
    @Override
    public void closeRefSite() {
        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.DIE, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        com.close();
    }

    /**
     * Signal Playground a new trial is starting.
     * @return Current trial.
     */
    @Override
    public int startTrialPlayground() {

        ClientCom com = initCom(MessageParticipant.PLAYGROUND);
        MessageToPlayground outMessage = new MessageToPlayground(MessageType.START_TRIAL_PLAY, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_STARTED_PLAYGROUND)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));

        return inMessage.getIntResponse();
    }

    /**
     * Wait for trial decision on playground.
     * @return true if trial finished, false otherwise.
     * @throws InterruptedException the thread was interrupted.
     */
    @Override
    public boolean assertTrialDecisionPlayground() throws InterruptedException {

        ClientCom com = initCom(MessageParticipant.PLAYGROUND);
        MessageToPlayground outMessage = new MessageToPlayground(MessageType.ASSERT_TRIAL_DECISION_PLAY, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_DECIDED_PLAYGROUND)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return inMessage.getBooleanResponse();
    }

    /**
     * Signal playground that a new game is starting.
     */
    @Override
    public void announceNewGamePlayground() {

        ClientCom com = initCom(MessageParticipant.PLAYGROUND);
        MessageToPlayground outMessage = new MessageToPlayground(MessageType.ANNOUNCE_NEW_GAME_PLAY, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GAME_ANNOUNCED_PLAY)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

    }

    /**
     * Signal referee site that a new game is starting.
     */
    @Override
    public void announceNewGameRefSite() {

        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.ANNOUNCE_NEW_GAME_REFSITE, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GAME_ANNOUNCED_REFSITE)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));

    }

    /**
     * Declare game winner.
     * @param currentTrial Current trial.
     * @param ropePos Rope Position.
     * @param knockout True if game was over due to knockout.
     * @return true if game was over due to knockout.
     */
    @Override
    public boolean declareGameWinner(int currentTrial, int ropePos, boolean knockout)
    {

        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.DECLARE_GAME_WINNER, MessageParticipant.REFEREE, currentTrial, ropePos, knockout);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GAME_WINNER_DECLARED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        referee.changeState(RefereeState.longName(inMessage.getStringResponse()));
        return inMessage.getBooleanResponse();
    }

    /**
     * Declare match winner based on Referee Site data.
     */
    @Override
    public void declareMatchWinner() {

        ClientCom com = initCom(MessageParticipant.REFSITE);
        MessageToRefSite outMessage = new MessageToRefSite(MessageType.DECLARE_MATCH_WINNER, MessageParticipant.REFEREE);

        com.writeObject(outMessage);
        MessageReply inMessage =  (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.MATCH_WINNER_DECLARED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        //referee.changeState(RefereeState.longName(inMessage.getStringResponse()));
    }

    /**
     * Get connection to remote host.
     * @param hostID Remote host ID.
     * @return connection to the remote host.
     */
    private ClientCom initCom(int hostID) {
        ClientCom com;

        switch (hostID) {
            case MessageParticipant.BENCH:
                com = new ClientCom(configs.getBenchHostname(), configs.getBenchPort());
                break;

            case MessageParticipant.PLAYGROUND:
                com = new ClientCom(configs.getPlaygroundHostname(), configs.getPlaygroundPort());
                break;

            case MessageParticipant.REFSITE:
                com = new ClientCom(configs.getRefSiteHostName(), configs.getRefSitePort());
                break;

            default:
                throw new IllegalArgumentException("ID do host inválido!");
        }

        while (!com.open ())
        {
            try { Thread.sleep ((long) (1000)); }
            catch (InterruptedException e) {}
        }
        return com;
    }
}
