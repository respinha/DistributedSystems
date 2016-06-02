package pt.ua.sd.ropegame.refereesite;


import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.interfaces.IRequestHandler;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.playground.Playground;

/**
 **
 * This module handles all message requests from any client to {@link RefereeSiteServer} to access {@link RefereeSite}
 */
public class RefereeSiteRequestHandler implements IRequestHandler {

    private RefereeSite refereeSite;

    /**
     * Constructor.
     * @param refereeSite A {@link RefereeSite} monitor.
     */
    public RefereeSiteRequestHandler(RefereeSite refereeSite) {
        this.refereeSite = refereeSite;
    }

    /**
     * Method in which all message requests are handled, processing their operations on the {@link RefereeSite} monitor and creating its respective reply.
     * @param message Message sent from client.
     * @return Reply message.
     */
    @Override
    public Message processAndReply(Message message) {

        MessageToRefSite inMessage = (MessageToRefSite) message;
        Message outMessage = null;
        try {
            if (inMessage.getMsgType() < 0) throw new MessageException("Tipo de mensagem inválido!", inMessage);
            if (inMessage.senderID() < 0) throw new MessageException("ID de entidade inválido!", inMessage);

            int trial, ropePos;
            boolean knockout;
            switch (inMessage.getMsgType()) {

                // estratégia para já: retornar o state numa mensagem de reply
                case MessageType.START_MATCH:
                    if (inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    refereeSite.startTheMatch();
                    outMessage = new MessageReply(MessageType.MATCH_STARTED, MessageParticipant.REFSITE, RefereeState.START_OF_THE_MATCH.shortName());
                    break;

                case MessageType.ANNOUNCE_NEW_GAME_REFSITE:
                    if(inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    refereeSite.announceNewGameRefSite();
                    outMessage = new MessageReply(MessageType.GAME_ANNOUNCED_REFSITE, MessageParticipant.REFSITE, RefereeState.START_OF_A_GAME.shortName());
                    break;

                case MessageType.START_TRIAL_REF:
                    if(inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    refereeSite.startTrialRefSite();
                    outMessage = new MessageReply(MessageType.TRIAL_STARTED_REFSITE, MessageParticipant.REFSITE, RefereeState.TEAMS_READY.shortName());
                    break;

                case MessageType.ASSERT_TRIAL_DECISION_REF:
                     if(inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    trial = inMessage.getCurrentTrial();
                    knockout = inMessage.isKnockout();

                    // referee only changes state in this method if it is in the end of a game
                    boolean endOfGame = !refereeSite.assertTrialDecisionRefSite(trial, knockout);
                    //if(endOfGame)
                        outMessage = new MessageReply(MessageType.TRIAL_DECIDED_REFSITE, MessageParticipant.REFSITE, endOfGame, RefereeState.END_OF_A_GAME.shortName());
                    //else
                      //  outMessage = new MessageReply(MessageType.TRIAL_DECIDED_REFSITE, MessageParticipant.REFSITE);
                    break;

                case MessageType.DECLARE_GAME_WINNER:
                     if(inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    trial = inMessage.getCurrentTrial();
                    ropePos = inMessage.getRopePos();
                    knockout = inMessage.isKnockout();

                    boolean endOfMatch = refereeSite.declareGameWinner(trial, ropePos, knockout);
                    String state = endOfMatch ? RefereeState.END_OF_THE_MATCH.shortName() : RefereeState.START_OF_A_GAME.shortName();
                    outMessage = new MessageReply(MessageType.GAME_WINNER_DECLARED, MessageParticipant.REFSITE, endOfMatch, state);

                    break;

                case MessageType.DECLARE_MATCH_WINNER:
                     if(inMessage.senderID() != MessageParticipant.REFEREE)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    refereeSite.declareMatchWinner();

                    // todo: como terminar o ciclo de vida do árbitro? fazê-lo do lado de quem?
                    outMessage = new MessageReply(MessageType.MATCH_WINNER_DECLARED, MessageParticipant.REFSITE);
                    break;

                case MessageType.REF_HAS_MORE_OPER:
                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de entidade inválido!", inMessage);

                    boolean hasOperations = refereeSite.refHasMoreOperations();

                    outMessage = new MessageReply(MessageType.REPLY_REF_HAS_MORE_OPER, MessageParticipant.REFSITE, hasOperations);

                    break;

                case MessageType.INFORM_REF:
                     if(inMessage.senderID() != MessageParticipant.COACH)
                        throw new MessageException("ID de entidade inválido!", inMessage);

                    int teamID = inMessage.getTeamID();
                    if (teamID < 0)
                        throw new MessageException("ID de equipa do treinador inválido!", inMessage);

                    refereeSite.informReferee(teamID);
                    outMessage = new MessageReply(MessageType.REF_INFORMED, MessageParticipant.REFSITE, CoachState.WATCH_TRIAL.shortName());
                    break;
                case MessageType.DIE:
                    refereeSite.closeRefSite();
                    throw new MessageException("A ligação terminou.", message);
            }
        } catch(MessageException e) {
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
