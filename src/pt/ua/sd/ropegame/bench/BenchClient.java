package pt.ua.sd.ropegame.bench;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.interfaces.IBenchGenRep;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.interfaces.IClient;
import pt.ua.sd.ropegame.team.Coach;

/**
 * BenchClient is the client that establishes connection between this machine and the {@Link GeneralRepositoryServer}.
 */
public class BenchClient extends ClientAbstract implements IBenchGenRep, IClient {

    /**
     * Constructor.
     * @param serverHostName The {@Link GeneralRepositoryServer} host name.
     * @param serverPortNumber The {@Link GeneralRepositoryServer} address port number.
     */
    public BenchClient(String serverHostName, int serverPortNumber) {
        super(serverHostName, serverPortNumber);

    }

    /**
     * Message sent from {@link Bench} to a remote General Repository requesting to remove a contestant from its position in the logging file.
     * @param team Contestant team.
     * @param pos Contestant position.
     */
    @Override
    public void removeContestantFromPosition(int team, int pos) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.REMOVE_CONTESTANTS, MessageParticipant.BENCH, team, pos);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CONTESTANTS_REMOVED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Bench} to a remote GeneralRepository Server requesting to update contestants strengths in the logging file.
     * @param teamID
     * @param strengths
     */
    @Override
    public void updateStrengths(int teamID, int[] strengths) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_STRENGTHS, MessageParticipant.BENCH, teamID, strengths);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.STRENGTHS_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     *  Get connection to remote host {@Link GeneralRepositoryServer}.
     * @return connection to the remote host.
     */
    private ClientCom initCom() {

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
     * Message sent from {@link Bench} to a remote GeneralRepository Server requesting to update a {@Link Coach} state in the logging file.
     * @param state
     * @param teamID
     */
    @Override
    public void updateCoachState(String state, int teamID) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_COACH_STATE, MessageParticipant.BENCH, teamID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.COACH_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Bench} to a remote GeneralRepository Server requesting to update a {@Link Contestant} state in the logging file.
     * @param state
     * @param gameMemberID
     * @param teamID
     */

    @Override
    public void updateContestantState(String state, int gameMemberID, int teamID) {

        ClientCom com = initCom();

        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_CONT_STATE, MessageParticipant.BENCH, gameMemberID, teamID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CONT_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Bench} to a remote GeneralRepository Server requesting to update a {@Link Referee} state in the logging file.
     * @param state
     */
    @Override
    public void updateRefState(String state) {

        ClientCom com = initCom();

        Message inMessage;
        MessageToGenRep outMessage = new MessageToGenRep(MessageType.UPDATE_REF_STATE, MessageParticipant.BENCH, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REF_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link Bench} to a remote GeneralRepository Server announcing the end of its operations.
     * @return
     */
    @Override
    public boolean requestToDie() {
        ClientCom com = initCom();

        MessageToGenRep outMessage = new MessageToGenRep(MessageType.DIE, MessageParticipant.BENCH);
        com.writeObject(outMessage);
        com.close();

        return true;
    }
}
