package pt.ua.sd.ropegame.refereesite;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.interfaces.IClient;
import pt.ua.sd.ropegame.common.interfaces.IRefSiteGenRep;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.playground.Playground;

/**
 * RefereeSiteClient is the client that establishes connection between this machine and the {@Link GeneralRepositoryServer}.
 */
public class RefereeSiteClient extends ClientAbstract implements IRefSiteGenRep, IClient {

    /**
     * Constructor.
     * @param serverHostName The {@Link GeneralRepositoryServer} host name.
     * @param serverPortNumber The {@Link GeneralRepositoryServer} address port number.
     */
    public RefereeSiteClient(String serverHostName, int serverPortNumber) {
        super(serverHostName, serverPortNumber);
    }

    @Override
    public void updateGame(int game) {

        ClientCom com = initCom();
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_GAME, MessageParticipant.REFSITE, game);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REPLY_UPDATE_GAME)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link RefereeSite} to a remote GeneralRepository Server requesting to update the match winner in the logging file.
     * @param winner
     * @param results
     */
    @Override
    public void updateMatchWinner(int winner, int[] results) {

        ClientCom com = initCom();
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_MATCH_WINNER, MessageParticipant.REFSITE, winner, results);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.MATCH_WINNER_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link RefereeSite} to a remote GeneralRepository Server requesting to update the game winner in the logging file.
     * @param currentGame
     * @param gameWinner
     * @param ntrials
     * @param knockout
     */
    @Override
    public void updateGameWinner(int currentGame, int gameWinner, int ntrials, boolean knockout) {

        ClientCom com = initCom();
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_GAME_WINNER, MessageParticipant.REFSITE, currentGame, gameWinner, ntrials, knockout);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GAME_WINNER_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link RefereeSite} to a remote GeneralRepository Server requesting to finnish writing to the logging file.
     */
    @Override
    public void generateLogFile() {

        ClientCom com = initCom();
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.GENERATE_LOG, MessageParticipant.REFSITE);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.LOG_GENERATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }


    /**
     * Message sent from {@link RefereeSite} to a remote GeneralRepository Server requesting to update a {@link pt.ua.sd.ropegame.team.Coach} state in the logging file.
     * @param state
     * @param teamID
     */
    @Override
    public void updateCoachState(String state, int teamID) {

        ClientCom com = initCom();
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_COACH_STATE, MessageParticipant.REFSITE, teamID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.COACH_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
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
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_CONT_STATE, MessageParticipant.REFSITE, teamID, gameMemberID, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CONT_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
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
        Message inMessage, outMessage;
        outMessage = new MessageToGenRep(MessageType.UPDATE_REF_STATE, MessageParticipant.REFSITE, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REF_UPDATED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from {@link RefereeSite} to a remote GeneralRepository Server announcing the end of its operations.
     * @return nothing
     */
    @Override
    public boolean requestToDie() {
        ClientCom com = initCom();

        MessageToGenRep outMessage = new MessageToGenRep(MessageType.DIE, MessageParticipant.REFSITE);
        com.writeObject(outMessage);
        com.close();

        return true;
    }

    /**
     *  Get connection to remote host {@Link GeneralRepositoryServer}.
     * @return connection to the remote host.
     */
    public ClientCom initCom() {

        ClientCom com = new ClientCom (serverHostName, serverPortNumber);

        while (!com.open ())
        { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
        }

        return com;
    }
}
