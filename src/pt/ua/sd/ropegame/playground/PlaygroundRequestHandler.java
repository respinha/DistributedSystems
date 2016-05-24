package pt.ua.sd.ropegame.playground;

import genclass.GenericIO;
import pt.ua.sd.ropegame.bench.Bench;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.enums.CoachState;
import pt.ua.sd.ropegame.common.enums.ContestantState;
import pt.ua.sd.ropegame.common.enums.RefereeState;
import pt.ua.sd.ropegame.common.interfaces.IRequestHandler;

/**
 * This module handles all message requests from any client to {@link PlaygroundServer} to access {@link Playground}
 */
public class PlaygroundRequestHandler implements IRequestHandler {

    private Playground playground;

    /**
     * Constructor.
     * @param playground A {@link Playground} monitor.
     */
    public PlaygroundRequestHandler(Playground playground) {
        this.playground = playground;
    }

    /**
     * Method in which all message requests are handled, processing their operations on the {@link Playground} monitor and creating its respective reply.
     * @param message Message sent from client.
     * @return Reply message.
     */
    @Override
    public Message processAndReply(Message message) {

        MessageToPlayground inMessage = (MessageToPlayground) message;
        Message outMessage = null;

        int gameMemberID, teamID, strength, position, currentTrial;
        boolean knockout;
        try {

            if (inMessage.getMsgType() < 0) throw new MessageException("Tipo de mensagem inválido!", inMessage);
            if (inMessage.senderID() < 0 || (inMessage.senderID() != MessageParticipant.REFEREE && inMessage.getTeamID() < 0)) throw new MessageException("ID de entidade inválido!", inMessage);

            switch (inMessage.getMsgType()) {
                case MessageType.START_TRIAL_PLAY:
                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de entidade inválido!", inMessage);

                    currentTrial = playground.startTrialPlayground();
                    outMessage = new MessageReply(MessageType.TRIAL_STARTED_PLAYGROUND, MessageParticipant.PLAYGROUND, currentTrial, RefereeState.WAIT_FOR_TRIAL_CONCLUSION.shortName());
                    break;
                case MessageType.ANNOUNCE_NEW_GAME_PLAY:
                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de entidade inválido!", inMessage);

                    playground.announceNewGamePlayground();
                    outMessage = new MessageReply(MessageType.GAME_ANNOUNCED_PLAY, MessageParticipant.PLAYGROUND);

                    break;
                case MessageType.ASSERT_TRIAL_DECISION_PLAY:
                    if(inMessage.senderID() != MessageParticipant.REFEREE) throw new MessageException("ID de entidade inválido!", inMessage);

                    knockout = playground.assertTrialDecisionPlayground();
                    outMessage = new MessageReply(MessageType.TRIAL_DECIDED_PLAYGROUND, MessageParticipant.PLAYGROUND, knockout);
                    break;
                case MessageType.STAND_IN_LINE:
                    if(inMessage.senderID() != MessageParticipant.CONTESTANT) throw new MessageException("ID de entidade inválido!", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    strength = inMessage.getStrength();
                    teamID = inMessage.getTeamID();
                    position = playground.standInLine(gameMemberID, teamID, strength);
                    outMessage = new MessageReply(MessageType.IN_PLAYGROUND, MessageParticipant.PLAYGROUND, position, ContestantState.STAND_IN_POSITION.shortName());
                    break;
                case MessageType.GET_READY:
                    if(inMessage.senderID() != MessageParticipant.CONTESTANT) throw new MessageException("ID de entidade inválido!", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    teamID = inMessage.getTeamID();
                    strength = inMessage.getStrength();

                    playground.getReady(gameMemberID, teamID, strength);

                    outMessage = new MessageReply(MessageType.READY, MessageParticipant.PLAYGROUND, strength, ContestantState.DO_YOUR_BEST.shortName());
                    break;
                case MessageType.PULL_THE_ROPE:
                    if(inMessage.senderID() != MessageParticipant.CONTESTANT) throw new MessageException("ID de entidade inválido!", inMessage);

                    outMessage = new MessageReply(MessageType.PULLED, MessageParticipant.PLAYGROUND);
                    break;
                case MessageType.AM_DONE:
                    if(inMessage.senderID() != MessageParticipant.CONTESTANT) throw new MessageException("ID de entidade inválido!", inMessage);

                    boolean gameOver = playground.amDone();

                    // e envia isso ao bench para o benhc atualizar essa informação em seatDown
                    outMessage = new MessageReply(MessageType.DONE, MessageParticipant.PLAYGROUND, gameOver);
                    break;
                case MessageType.MOVE_COACH:
                    if(inMessage.senderID() != MessageParticipant.COACH) throw new MessageException("ID de entidade inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    int coachToInform = playground.moveCoachToPlayground(teamID);

                    outMessage = new MessageReply(MessageType.COACH_MOVED, MessageParticipant.PLAYGROUND, coachToInform, CoachState.WATCH_TRIAL.shortName());
                    break;
                case MessageType.REVIEW_NOTES_PLAY:
                    if(inMessage.senderID() != MessageParticipant.COACH) throw new MessageException("ID de entidade inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    int strategy = playground.reviewNotes(teamID);

                    outMessage = new MessageReply(MessageType.REVIEW_DONE_PLAY, MessageParticipant.PLAYGROUND, strategy);

                    break;
                case MessageType.GET_CURRENT_TRIAL:
                    if(inMessage.senderID() != MessageParticipant.COACH) throw new MessageException("ID de entidade inválido!", inMessage);

                    int trial = playground.getCurrentTrial();
                    outMessage = new MessageReply(MessageType.GOT_CURRENT_TRIAL, MessageParticipant.PLAYGROUND, trial);
                    break;
                case MessageType.GET_ROPE_POS:

                    int ropePos = playground.getRopePos();
                    outMessage = new MessageReply(MessageType.ROPE_POS, MessageParticipant.PLAYGROUND, ropePos);
                    break;
                case MessageType.GET_IS_KNOCKOUT:

                    knockout = playground.isKnockout();
                    outMessage = new MessageReply(MessageType.GOT_IS_KNOCKOUT, MessageParticipant.PLAYGROUND, knockout);
                    break;

                case MessageType.DIE:
                    boolean dead = playground.closePlaygroundConnection();

                    if(dead) throw new MessageException("A ligação terminou.", message);
            }
        } catch (MessageException e) {
            if(e.getMessageVal().getMsgType() == MessageType.DIE) {
                GenericIO.writelnString("O servidor foi desligado.");
                System.exit(0);
            }
            outMessage = e.getMessageVal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return outMessage;
    }
}
