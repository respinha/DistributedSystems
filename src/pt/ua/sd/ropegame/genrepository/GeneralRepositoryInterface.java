package pt.ua.sd.ropegame.genrepository;


import pt.ua.sd.ropegame.bench.Bench;
import pt.ua.sd.ropegame.bench.BenchServer;
import pt.ua.sd.ropegame.common.communication.*;
import pt.ua.sd.ropegame.common.interfaces.IRequestHandler;

/**
 * This module handles all message requests from any client to {@link GeneralRepositoryServer} to access {@link GeneralRepository}
 */
public class GeneralRepositoryInterface implements IRequestHandler {

    GeneralRepository generalRepository;

    /**
     * Constructor
     * @param generalRepository A {@link GeneralRepository} monitor.
     */
    public GeneralRepositoryInterface(GeneralRepository generalRepository) {
        this.generalRepository = generalRepository;
    }

    /**
     * Method in which all message requests are handled, processing their operations on the {@link GeneralRepository} monitor and creating its respective reply.
     * @param message Message sent from client.
     * @return Reply message.
     */
    @Override
    public Message processAndReply(Message message) {

        MessageToGenRep inMessage = (MessageToGenRep) message;
        Message outMessage = null;

        try {

            if(inMessage.senderID() < MessageParticipant.BENCH) throw new MessageException("ID inválido! Mensagem não foi enviada por Bench, Playground ou Referee Site", inMessage);

            int gameMemberID,teamID, position, trial, ropePos, game, winner;
            int[] results;
            boolean knockout;

            switch (inMessage.getMsgType()) {

                case MessageType.UPDATE_REF_STATE:
                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND && inMessage.senderID() != MessageParticipant.BENCH && inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    if (inMessage.getState() == null || inMessage.getState().equals(""))
                        throw new MessageException("Estado inexistente!", inMessage);

                    generalRepository.updateRefState(inMessage.getState());
                    outMessage = new MessageReply(MessageType.REF_UPDATED, MessageParticipant.GENREP);

                    break;
                case MessageType.UPDATE_CONT_STATE:
                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND && inMessage.senderID() != MessageParticipant.BENCH && inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);
                    if (inMessage.getState() == null || inMessage.getState().equals(""))
                        throw new MessageException("Estado inexistente!", inMessage);


                    gameMemberID = inMessage.getGameMemberID();
                    if (gameMemberID == MessageParticipant.REFEREE) throw new MessageException("ID inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    if (teamID == -1) throw new MessageException("ID de equipa inválida", inMessage);

                    int strength = inMessage.getStrength();
                    if (strength == -1) throw new MessageException("Força inválida!", inMessage);
                    generalRepository.updateContestantState(inMessage.getState(), gameMemberID, teamID);

                    outMessage = new MessageReply(MessageType.CONT_UPDATED, MessageParticipant.GENREP);
                    break;
                case MessageType.UPDATE_COACH_STATE:
                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND && inMessage.senderID() != MessageParticipant.BENCH && inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);
                    if (inMessage.getState() == null || inMessage.getState().equals(""))
                        throw new MessageException("Estado inexistente!", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    if (gameMemberID == MessageParticipant.REFEREE) throw new MessageException("ID inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    if (teamID == -1) throw new MessageException("ID de equipa inválido!", inMessage);

                    generalRepository.updateCoachState(inMessage.getState(), teamID);

                    outMessage = new MessageReply(MessageType.COACH_UPDATED, MessageParticipant.GENREP);
                    break;
                case MessageType.REMOVE_CONTESTANTS:
                    if (inMessage.senderID() != MessageParticipant.BENCH)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    teamID = inMessage.getTeamID();
                    if (teamID == -1) throw new MessageException("ID de equipa inválido!", inMessage);

                    position = inMessage.getContestantPosition();
                    if (Math.abs(position) > 3) throw new MessageException("Posição inválida!", inMessage);

                    generalRepository.removeContestantFromPosition(teamID, position);

                    outMessage = new MessageReply(MessageType.CONTESTANTS_REMOVED, MessageParticipant.GENREP);
                    break;
                case MessageType.UPDATE_CONT_POSITION:

                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    gameMemberID = inMessage.getGameMemberID();
                    if (gameMemberID == MessageParticipant.REFEREE) throw new MessageException("ID inválido!", inMessage);

                    teamID = inMessage.getTeamID();
                    if (teamID == -1) throw new MessageException("ID de equipa inválido!", inMessage);

                    position = inMessage.getContestantPosition();
                    if (Math.abs(position) > 3) throw new MessageException("Posição inválida!", inMessage);

                    generalRepository.updateContestantPosition(gameMemberID, teamID, position);

                    outMessage = new MessageReply(MessageType.POSITION_UPDATED, MessageParticipant.GENREP);
                    break;

                case MessageType.UPDATE_TRIAL:
                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    trial = inMessage.getCurrentTrial();
                    generalRepository.updateTrial(trial);

                    outMessage = new MessageReply(MessageType.TRIAL_UPDATED, MessageParticipant.GENREP);
                    break;

                case MessageType.UPDATE_ROPE_POSITION:
                    if (inMessage.senderID() != MessageParticipant.PLAYGROUND)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    ropePos = inMessage.getRopePos();
                    generalRepository.updateRopePosition(ropePos);

                    outMessage = new MessageReply(MessageType.ROPE_UPDATED, MessageParticipant.GENREP);
                    break;
                case MessageType.UPDATE_GAME_WINNER:
                    if (inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    game = inMessage.getGame();
                    winner = inMessage.getWinner();
                    trial = inMessage.getCurrentTrial();
                    knockout = inMessage.isKnockout();

                    generalRepository.updateGameWinner(game, winner, trial, knockout);

                    outMessage = new MessageReply(MessageType.GAME_WINNER_UPDATED, MessageParticipant.GENREP);
                    break;
                case MessageType.UPDATE_MATCH_WINNER:
                    if (inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    winner = inMessage.getWinner();
                    results = inMessage.getResults();
                    generalRepository.updateMatchWinner(winner, results);

                    outMessage = new MessageReply(MessageType.MATCH_WINNER_UPDATED, MessageParticipant.GENREP);
                    break;
                case MessageType.GENERATE_LOG:
                    if (inMessage.senderID() != MessageParticipant.REFSITE)
                        throw new MessageException("ID " + inMessage.senderID() + " inválido", inMessage);

                    generalRepository.generateLogFile();

                    outMessage = new MessageReply(MessageType.LOG_GENERATED, MessageParticipant.GENREP);

                    break;

                case MessageType.UPDATE_STRENGTHS:

                    teamID = inMessage.getTeamID();
                    int[] strengths = inMessage.getStrengths();
                    generalRepository.updateStrengths(teamID, strengths);
                    outMessage = new MessageReply(MessageType.STRENGTHS_UPDATED, MessageParticipant.GENREP);
                    break;

                case MessageType.UPDATE_GAME:
                    game = inMessage.getGame();
                    generalRepository.updateGame(game);
                    outMessage = new MessageReply(MessageType.UPDATE_GAME, MessageParticipant.GENREP);
                    break;

                case MessageType.DIE:
                    boolean dead = generalRepository.requestToDie();
                    if(dead) throw new MessageException("A ligação terminou.", message);
                    break;

            }
        } catch (MessageException e) {
            if(e.getMessageVal().getMsgType() == MessageType.DIE) {
                System.out.println("O servidor foi desligado.");
                System.exit(0);
            }

            outMessage = e.getMessageVal();
        }

        return outMessage;
    }
}
