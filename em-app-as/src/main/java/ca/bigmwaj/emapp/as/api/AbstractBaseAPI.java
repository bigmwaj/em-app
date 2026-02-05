package ca.bigmwaj.emapp.as.api;

import ca.bigmwaj.emapp.as.api.shared.Message;
import ca.bigmwaj.emapp.as.api.shared.MessageType;

public abstract class AbstractBaseAPI {

    protected Message _success(String message) {
        return new Message(MessageType.SUCCESS, message);
    }

    protected Message _warn(String message) {
        return new Message(MessageType.WARN, message);
    }

    protected Message _error(String message) {
        return new Message(MessageType.ERROR, message);
    }

}


