package pt.ua.sd.ropegame.bench;


import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.IRequestHandler;
import pt.ua.sd.ropegame.common.communication.*;

/**
 * This module handles all message requests from any client to {@link BenchServer} to access {@link Bench}
 */
public class BenchRequestHandler implements IRequestHandler {

    private Bench bench;

    /**
     * Constructor
     * @param bench A {@link Bench} monitor.
     */
    public BenchRequestHandler(Bench bench) {
        this.bench = bench;
    }

    /**
     * Method in which all message requests are handled, processing their operations on the {@link Bench} monitor and creating its respective reply.
     * @param message Message sent from client.
     * @return Reply message.
     */
    public Message processAndReply(Message message) {

        Message outMessage = null;
        MessageToBench inMessage = (MessageToBench) message;

        int trial, teamID, gameMemberID, position, strength;
        boolean knockout, hasOperations;

       try {
            if (inMessage.getMsgType() < 0) throw new MessageException("Tipo de mensagem inválido!", inMessage);
            if (inMessage.senderID() < 0) throw new MessageException("ID de entidade inválido!", inMessage);

            switch (inMessage.getMsgType()) {

                case MessageType.CALL_TRIAL:
                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de equipa inválido!", inMessage);

                    bench.callTrial();
                    outMessage = new MessageReply(MessageType.TRIAL_CALLED, MessageParticipant.BENCH, RefereeState.TEAMS_READY.shortName());
                    break;
                case MessageType.REVIEW_NOTES_BENCH:
                    if (inMessage.getTeamID() < 0) throw new MessageException("ID de equipa inválido!", inMessage);
                    if (inMessage.getCurrentTrial() < 0)
                        throw new MessageException("Coach enviou um trial inválido!", inMessage);


                    teamID = inMessage.getTeamID();
                    trial = inMessage.getCurrentTrial();
                    knockout = inMessage.isKnockout();

                    bench.reviewNotes(teamID, trial, knockout);


                    outMessage = new MessageReply(MessageType.REVIEW_DONE_BENCH, MessageParticipant.BENCH, CoachState.WAIT_FOR_REFEREE_COMMAND.shortName());
                    break;
                case MessageType.SEAT_DOWN:
                    if (inMessage.getTeamID() < 0) throw new MessageException("ID de equipa inválido!", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    teamID = inMessage.getTeamID();
                    strength = inMessage.getStrength();
                    position = inMessage.getContestantPosition();
                    boolean matchOver = inMessage.gameIsOver();


                    bench.seatDown(gameMemberID, teamID, strength, position, matchOver);

                    outMessage = new MessageReply(MessageType.SEATED_DONE, MessageParticipant.BENCH, ContestantState.SEAT_AT_THE_BENCH.shortName());
                    break;
                case MessageType.CHECK_FOR_CONT_CALL:
                    if (inMessage.getTeamID() < 0) throw new MessageException("ID de equipa inválido!", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    teamID = inMessage.getTeamID();
                    strength = bench.waitForContestantCall(gameMemberID, teamID);

                    outMessage = new MessageReply(MessageType.CALLED_TO_GAME, MessageParticipant.BENCH, strength);
                    break;
                case MessageType.WAIT_FOR_COACH_CALL:
                    if (inMessage.getTeamID() < 0) throw new MessageException("ID de equipa inválido!", inMessage);


                    bench.waitForCoachCall();

                    outMessage = new MessageReply(MessageType.COACH_CALLED, MessageParticipant.BENCH);
                    break;
                case MessageType.CALL_CONTESTANTS:
                    if (inMessage.getTeamID() < 0) throw new MessageException("ID de equipa inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    String strategy = inMessage.getStrategy();
                    bench.callContestants(teamID, strategy);

                    outMessage = new MessageReply(MessageType.CONTESTANTS_CALLED, MessageParticipant.BENCH, CoachState.ASSEMBLE_TEAM.shortName());
                    break;
                case MessageType.NOTIFY_CONTESTANTS:

                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de equipa inválido!", inMessage);

                    //bench.notifyContestantsMatchIsOver();

                    outMessage = new MessageReply(MessageType.MATCH_OVER_CONTESTANTS, MessageParticipant.BENCH, RefereeState.END_OF_THE_MATCH.shortName());
                    break;

                case MessageType.CONT_HAS_MORE_OPER:
                    if(inMessage.senderID() != MessageParticipant.CONTESTANT) throw new MessageException("ID de equipa inválido!", inMessage);

                    hasOperations = bench.contestantsHaveMoreOperations();

                    outMessage = new MessageReply(MessageType.REPLY_CONT_HAS_MORE_OPER, MessageParticipant.BENCH, hasOperations);
                    break;

                case MessageType.COACH_HAS_MORE_OPER:
                    if(inMessage.senderID() != MessageParticipant.COACH) throw new MessageException("ID de equipa inválido!", inMessage);

                    hasOperations = bench.coachesHaveMoreOperations();

                    outMessage = new MessageReply(MessageType.REPLY_COACH_HAS_MORE_OPER, MessageParticipant.BENCH, hasOperations);
                    break;

                case MessageType.DIE:
                    boolean dead = bench.closeBenchConnection();
                    if(dead) throw new MessageException("A ligação terminou.", message);
                    break;
            }
        } catch (MessageException e) {
           if(e.getMessageVal().getMsgType() == MessageType.DIE) {
               System.out.println("O servidor foi desligado.");

               System.exit(0);
           }
           outMessage = e.getMessageVal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return outMessage;
    }
}
