package pt.ua.sd.ropegame.common.communication;

/**
 * Message request types.
 * */
public class MessageType {
    // Referee Message types
    public static final int START_MATCH = 100;
    public static final int MATCH_STARTED = 101;
    public static final int ANNOUNCE_NEW_GAME_PLAY = 102;
    public static final int GAME_ANNOUNCED_PLAY = 103;
    public static final int ANNOUNCE_NEW_GAME_REFSITE = 104;
    public static final int GAME_ANNOUNCED_REFSITE = 105;
    public static final int CALL_TRIAL = 106;
    public static final int TRIAL_CALLED = 107;
    public static final int START_TRIAL_REF = 108;
    public static final int START_TRIAL_PLAY = 109;
    public static final int TRIAL_STARTED_REFSITE = 110;
    public static final int ASSERT_TRIAL_DECISION_REF = 111;
    public static final int ASSERT_TRIAL_DECISION_PLAY = 112;
    public static final int TRIAL_DECIDED_PLAYGROUND = 113;
    public static final int TRIAL_DECIDED_REFSITE = 121;
    public static final int DECLARE_GAME_WINNER = 114;
    public static final int GAME_WINNER_DECLARED = 115;
    public static final int DECLARE_MATCH_WINNER = 116;
    public static final int MATCH_WINNER_DECLARED = 117;
    public static final int NOTIFY_CONTESTANTS = 118;
    public static final int MATCH_OVER_CONTESTANTS = 119;
    public static final int TRIAL_STARTED_PLAYGROUND = 120;

    // Coach Message types
    public static final int REVIEW_NOTES_BENCH = 200;
    public static final int REVIEW_DONE_BENCH = 201;
    public static final int REVIEW_NOTES_PLAY = 202;
    public static final int REVIEW_DONE_PLAY = 203;
    public static final int WAIT_FOR_COACH_CALL = 204;
    public static final int COACH_CALLED = 205;
    public static final int CALL_CONTESTANTS = 206;
    public static final int CONTESTANTS_CALLED = 207;
    public static final int MOVE_COACH = 208;
    public static final int COACH_MOVED = 209;
    public static final int INFORM_REF = 210;
    public static final int REF_INFORMED = 211;
    public static final int COACH_HAS_MORE_OPER = 212;
    public static final int REPLY_COACH_HAS_MORE_OPER = 213;
    public static final int GET_CURRENT_TRIAL = 214;
    public static final int GOT_CURRENT_TRIAL = 215;
    public static final int GET_IS_KNOCKOUT = 216;
    public static final int GOT_IS_KNOCKOUT = 217;

    // Contestant Message types
    public static final int SEAT_DOWN = 300;
    public static final int SEATED_DONE = 301;
    public static final int CHECK_FOR_CONT_CALL = 302;
    public static final int CALLED_TO_GAME = 303;
    public static final int STAND_IN_LINE = 304;
    public static final int IN_PLAYGROUND = 305;
    public static final int GET_READY = 306;
    public static final int READY = 307;
    public static final int PULL_THE_ROPE = 308;
    public static final int PULLED = 309;
    public static final int AM_DONE = 310;
    public static final int DONE = 311;
    public static final int CALL_TO_END = 312;
    public static final int CONT_HAS_MORE_OPER = 313;
    public static final int REPLY_CONT_HAS_MORE_OPER = 314;


    // Repository messages sent by all the other MidServers
    public static final int UPDATE_COACH_STATE = 400;
    public static final int COACH_UPDATED = 401;
    public static final int UPDATE_CONT_STATE = 402;
    public static final int CONT_UPDATED = 403;
    public static final int UPDATE_REF_STATE = 404;
    public static final int REF_UPDATED = 405;

    // BenchToRep messages
    public static final int REMOVE_CONTESTANTS = 500;
    public static final int CONTESTANTS_REMOVED = 501;
    public static final int UPDATE_STRENGTHS = 502;
    public static final int STRENGTHS_UPDATED = 502;

    // PlaygroundToRep messages
    public static final int UPDATE_CONT_POSITION = 600;
    public static final int POSITION_UPDATED = 601;
    public static final int UPDATE_TRIAL = 602;
    public static final int TRIAL_UPDATED = 603;
    public static final int UPDATE_ROPE_POSITION = 604;
    public static final int ROPE_UPDATED = 605;
    public static final int GET_ROPE_POS = 606;
    public static final int ROPE_POS = 607;


    // RefereeSiteToRep messages
    public static final int UPDATE_GAME_WINNER = 700;
    public static final int GAME_WINNER_UPDATED = 701;
    public static final int UPDATE_MATCH_WINNER = 702;
    public static final int MATCH_WINNER_UPDATED = 703;
    public static final int GENERATE_LOG = 704;
    public static final int LOG_GENERATED = 705;
    public static final int REF_HAS_MORE_OPER = 706;
    public static final int REPLY_REF_HAS_MORE_OPER = 707;

    public static final int DIE = 999;
}
