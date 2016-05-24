package pt.ua.sd.ropegame.common.interfaces;

import pt.ua.sd.ropegame.common.communication.Message;

public interface IRequestHandler {

    Message processAndReply(Message inMessage);
}
