package pt.ua.sd.ropegame.playground;


import pt.ua.sd.ropegame.bench.Bench;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.interfaces.IPlaygroundGenRep;

/**
 * PlaygroundClient is the client that establishes connection between this machine and the {@Link GeneralRepositoryServer}.
 */
public class PlaygroundClient extends ClientAbstract implements IPlaygroundGenRep {

    /**
     * Constructor.
     * @param serverHostName The {@Link GeneralRepositoryServer} host name.
     * @param serverPortNumber The {@Link GeneralRepositoryServer} address port number.
     */
    public PlaygroundClient(String serverHostName, int serverPortNumber) {
        super(serverHostName, serverPortNumber);
    }

    /**
     *  Get connection to remote host {@Link GeneralRepositoryServer}.
     * @return connection to the remote host.
     */
    public ClientCom initCom() {
        ClientCom com = new ClientCom(serverHostName, serverPortNumber);

        while (!com.open ())
        { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
        }
        return com;
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update a {@link pt.ua.sd.ropegame.team.Contestant} playground position in the logging file.
     * @param contestantID
     * @param teamID
     * @param position
     */
    @Override
    public void updateContestantPosition(int contestantID, int teamID, int position) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_CONT_POSITION, MessageParticipant.PLAYGROUND, contestantID, teamID, position);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.POSITION_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update the current trial in the logging file.
     * @param trial
     */
    @Override
    public void updateTrial(int trial) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_TRIAL, MessageParticipant.PLAYGROUND, trial);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.TRIAL_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update the current rope position in the logging file.
     * @param ropePos
     */
    @Override
    public void updateRopePosition(int ropePos) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_ROPE_POSITION, MessageParticipant.PLAYGROUND, ropePos);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.ROPE_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update the current  in the logging file.
     * @param state
     * @param teamID
     */

    @Override
    public void updateCoachState(String state, int teamID) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_COACH_STATE, MessageParticipant.PLAYGROUND, teamID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.COACH_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update a {@link pt.ua.sd.ropegame.team.Contestant} state in the logging file.
     * @param state
     * @param gameMemberID
     * @param teamID
     */
    @Override
    public void updateContestantState(String state, int gameMemberID, int teamID) {


        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_CONT_STATE, MessageParticipant.PLAYGROUND, gameMemberID, teamID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CONT_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server requesting to update a {@link pt.ua.sd.ropegame.referee.Referee} state in the logging file.
     * @param state
     */
    @Override
    public void updateRefState(String state) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_REF_STATE, MessageParticipant.PLAYGROUND, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REF_UPDATED)  {
            System.out.println("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Playground} to a remote GeneralRepository Server announcing the end of its operations.
     * @return nothing
     */
    @Override
    public boolean requestToDie() {
        ClientCom com = initCom();

        MessageToGenRep outMessage = new MessageToGenRep(MessageType.DIE, MessageParticipant.PLAYGROUND);
        com.writeObject(outMessage);
        com.close();

        return true;
    }
}
