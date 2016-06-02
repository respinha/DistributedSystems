package pt.ua.sd.ropegame.team;

import genclass.GenericIO;
import pt.ua.sd.ropegame.common.GameOfTheRopeConfigs;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.CoachStrategies;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.interfaces.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A team is composed by a {@link Coach} and multiple {@link Contestant} instances.
 * TeamClient is the client that establishes connection between this machine and the remote hosts.
 */
public class TeamClient
        implements ICoachRefSite, ICoachPlay, ICoachBench,
            IContestantsBench, IContestantsPlay, IVectClock
{

    // team contestants and coach
    private Contestant[] contestants;
    private Coach coach;
    private GameOfTheRopeConfigs configs;

    // Vectorial clock variables
    private int[] vectClocks;
    private final int TEAM_CLOCK_INDEX;
    private Lock mutex;
    /**
     * Constructor for team Client.
     * @param team This team ID.
     * @param configs Game configuration.
     */
    public TeamClient(int team, GameOfTheRopeConfigs configs) {

        this.configs = configs;

        if(team == 0) TEAM_CLOCK_INDEX = 1;
        else TEAM_CLOCK_INDEX = 7;
        mutex = new ReentrantLock();

        coach = new Coach(this, this, this, team, new CoachStrategies());
        contestants = new Contestant[configs.getNContestants()];

        for (int j = 0; j < configs.getNContestants(); j++)
            contestants[j] = new Contestant(this, this, team, j);

    }

    /**
     * Start all threads.
     */
    public void start() {
        coach.start();

        for(int i = 0; i < configs.getNContestants(); i++)
            contestants[i].start();
    }

    /**
     * Join all threads.
     * @throws InterruptedException A thread was interrupted.
     */
    public void join() throws InterruptedException {
        coach.join();

        for(int i = 0; i < configs.getNContestants(); i++)
            contestants[i].join();
    }

    /**
     * Message sent from a {@link Coach} to a remote Bench Server to request reviewNotes operation.
     * @param teamID The coach team.
     * @param trial Current trial.
     * @param knockout True if game was won by knockout, false otherwise.
     */
    @Override
    public void reviewNotes(int teamID, int trial, boolean knockout) {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message outMessage;

        // updateCurrentTime(vectClocks, true, this.TEAM_CLOCK_INDEX);
        outMessage = new MessageToBench(MessageType.REVIEW_NOTES_BENCH, MessageParticipant.COACH, trial, teamID, knockout);

        MessageReply inMessage;
        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REVIEW_DONE_BENCH)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        coach.changeState(CoachState.longName(inMessage.getStringResponse()));

    }

    /**
     * Message sent from a {@link Coach} to a remote Bench Server to request callContesants operation.
     * @param teamID The coach team.
     * @param strategy The coach strategy.
     */
    @Override
    public void callContestants(int teamID, String strategy) {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message outMessage;
        MessageReply inMessage;
        outMessage = new MessageToBench(MessageType.CALL_CONTESTANTS, MessageParticipant.COACH, teamID, strategy);

        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CONTESTANTS_CALLED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        coach.changeState(CoachState.longName(inMessage.getStringResponse()));
    }

    /**
     * Message sent from a {@link Coach} to a remote Bench Server to request waitForCoachCall operation.
     */
    @Override
    public void waitForCoachCall() {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message inMessage, outMessage;

        outMessage = new MessageToBench(MessageType.WAIT_FOR_COACH_CALL, MessageParticipant.COACH);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.COACH_CALLED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }


    }

    /**
     * Message sent from a {@link Coach} to a remote Bench Server to request coachesHaveMoreOperations operation.
     * @return True if coaches have more operations, false otherwise.
     */
    @Override
    public boolean coachesHaveMoreOperations() {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message inMessage, outMessage;

        outMessage = new MessageToBench(MessageType.COACH_HAS_MORE_OPER, MessageParticipant.COACH);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REPLY_COACH_HAS_MORE_OPER)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getBooleanResponse();
    }

    /**
     * Message sent from a {@link Coach} to a remote Playground Server.
     * @return Current trial number.
     */
    @Override
    public int getCurrentTrial() {

        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message inMessage, outMessage;

        outMessage = new MessageToPlayground(MessageType.GET_CURRENT_TRIAL, MessageParticipant.COACH);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GOT_CURRENT_TRIAL)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getIntResponse();
    }

    /**
     * Message sent from a {@link Coach} to a remote Playground Server.
     * @param teamID This coach's team ID.
     * @return strategy The new Coach Strategy.
     */
    @Override
    public int reviewNotes(int teamID) {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message inMessage, outMessage;

        outMessage = new MessageToPlayground(MessageType.REVIEW_NOTES_PLAY, MessageParticipant.COACH, teamID);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REVIEW_DONE_PLAY)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getIntResponse();
    }

    /**
     * Message sent from a {@link Coach} to a remote Playground Server.
     * @param teamID This coach's team ID.
     * @return The ID of the team whose Coach should inform the referee that all the coaches are in the playground.
     */
    @Override
    public int moveCoachToPlayground(int teamID) throws InterruptedException {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message outMessage;
        MessageReply inMessage;
        outMessage = new MessageToPlayground(MessageType.MOVE_COACH, MessageParticipant.COACH, teamID);

        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.COACH_MOVED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        if(inMessage.getIntResponse() != teamID) coach.changeState(CoachState.longName(inMessage.getStringResponse()));
        return inMessage.getIntResponse();
    }

    /**
     * Message sent from a {@link Coach} to a remote Playground Server.
     * @return True if the game was won by knockout, false otherwise.
     */
    @Override
    public boolean isKnockout() {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message inMessage, outMessage;

        outMessage = new MessageToPlayground(MessageType.GET_IS_KNOCKOUT, MessageParticipant.COACH);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.GOT_IS_KNOCKOUT)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getBooleanResponse();
    }


    /**
     * Message sent from a {@link Coach} to a remote RefSite Server.
     * @param teamID This coach's team ID.
     */
    @Override
    public void informReferee(int teamID) {
        ClientCom com = initCom(MessageParticipant.REFSITE);

        Message outMessage;
        MessageReply inMessage;

        outMessage = new MessageToRefSite(MessageType.INFORM_REF, MessageParticipant.COACH, teamID);

        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REF_INFORMED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        coach.changeState(CoachState.longName(inMessage.getStringResponse()));
    }

    /**
     * Message sent from a {@link Contestant} to a remote Bench Server.
     * @param gameMemberID This contestant's ID.
     * @param teamID This contestant's team.
     * @return Contestant strength.
     */
    @Override
    public int waitForContestantCall(int gameMemberID, int teamID) {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message inMessage, outMessage;

        outMessage = new MessageToBench(MessageType.CHECK_FOR_CONT_CALL, MessageParticipant.CONTESTANT, teamID, gameMemberID);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.CALLED_TO_GAME)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getIntResponse();
    }

    /**
     * Message sent from a {@link Contestant} to a remote Bench Server.
     * @param gameMemberID This contestant's ID.
     * @param teamID This contestant's team.
     * @param strength This contestant's strength.
     * @param position This contestant's position.
     * @param matchOver
     */
    @Override
    public void seatDown(int gameMemberID, int teamID, int strength, int position, boolean matchOver) {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message inMessage, outMessage;

        outMessage = new MessageToBench(MessageType.SEAT_DOWN, MessageParticipant.CONTESTANT, teamID, gameMemberID, strength, position, matchOver);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.SEATED_DONE)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }


        String s = ((MessageReply) inMessage).getStringResponse();

        contestants[gameMemberID].changeState(ContestantState.longName(s));
    }

    /**
     * Message sent from a {@link Contestant} to a remote Bench Server.
     * @return True if contestant has more operations, false otherwise.
     */
    @Override
    public boolean contestantsHaveMoreOperations() {
        ClientCom com = initCom(MessageParticipant.BENCH);

        Message inMessage, outMessage;

        outMessage = new MessageToBench(MessageType.CONT_HAS_MORE_OPER, MessageParticipant.CONTESTANT);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.REPLY_CONT_HAS_MORE_OPER)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getBooleanResponse();
    }

    /**
     * Message sent by a contestant or a coach requesting the bench server to shut down.
     * @return always false
     */
    @Override
    public boolean closeBenchConnection() {
        ClientCom com = initCom(MessageParticipant.BENCH);
        Message outMessage = new MessageToBench(MessageType.DIE, MessageParticipant.CONTESTANT);
        com.writeObject(outMessage);
        com.close();

        return false;
    }

    /**
     * Message sent by a contestant or a coach requesting the playground server to shut down.
     * @return always false.
     */
    @Override
    public boolean closePlaygroundConnection() {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);
        Message outMessage = new MessageToPlayground(MessageType.DIE, MessageParticipant.COACH);
        com.writeObject(outMessage);
        com.close();

        return false;
    }

    /**
     * Message sent from a {@link Contestant} to a remote Playground Server.
     * @param gameMemberID This contestant's ID.
     * @param teamID This contestant's team.
     * @param strength This contestant's strength.
     */
    @Override
    public int standInLine(int gameMemberID, int teamID, int strength) {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message outMessage;

        MessageReply inMessage;
        outMessage = new MessageToPlayground(MessageType.STAND_IN_LINE, MessageParticipant.CONTESTANT, teamID, gameMemberID, strength);

        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.IN_PLAYGROUND)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        contestants[gameMemberID].changeState(ContestantState.longName(inMessage.getStringResponse()));

        return inMessage.getIntResponse();
    }

    /**
     * Message sent from a {@link Contestant} to a remote Playground Server.
     * @param gameMemberID This contestant's ID.
     * @param teamID This contestant's team.
     * @param strength This contestant's strength.
     */
    @Override
    public void getReady(int gameMemberID, int teamID, int strength) {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message outMessage;

        MessageReply inMessage;
        outMessage = new MessageToPlayground(MessageType.GET_READY, MessageParticipant.CONTESTANT, teamID, gameMemberID, strength);

        com.writeObject(outMessage);
        inMessage = (MessageReply) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.READY)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        contestants[gameMemberID].changeState(ContestantState.longName(inMessage.getStringResponse()));
    }

    /**
     * Message sent from a {@link Contestant} to a remote Playground Server.
     */
    @Override
    public void pullTheRope() {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message inMessage, outMessage;

        outMessage = new MessageToPlayground(MessageType.PULL_THE_ROPE, MessageParticipant.CONTESTANT);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.PULLED)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }
    }

    /**
     * Message sent from a {@link Contestant} to a remote Playground Server.
     */
    @Override
    public boolean amDone() throws InterruptedException {
        ClientCom com = initCom(MessageParticipant.PLAYGROUND);

        Message inMessage, outMessage;

        outMessage = new MessageToPlayground(MessageType.AM_DONE, MessageParticipant.CONTESTANT);

        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        com.close();

        if(inMessage.getMsgType() != MessageType.DONE)  {
            GenericIO.writelnString("Mensagem inesperada! " + inMessage.getMsgType());
            System.exit(1);
        }

        return ((MessageReply) inMessage).getBooleanResponse();
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

    @Override
    public void updateCurrentTime(int[] vectClocks, boolean self, int entityID) {
        mutex.lock();

        if(self) {
            this.vectClocks[entityID]++;
        } else {
            for (int i = 0; i < this.vectClocks.length; i++) this.vectClocks[i] = vectClocks[i];
        }

        mutex.lock();
    }


}
