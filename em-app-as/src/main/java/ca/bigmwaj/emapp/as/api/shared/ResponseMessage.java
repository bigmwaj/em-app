package ca.bigmwaj.emapp.as.api.shared;

import ca.bigmwaj.emapp.dm.dto.BaseDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseMessage<T extends BaseDto> {

    private final T data;

    private final List<Message> messages = new ArrayList<>();

    public ResponseMessage(T data) {
        this.data = data;
    }

    public ResponseMessage(Message message) {
        this.data = null;
        this.messages.add(message);
    }

    public void addSuccessMessage(String message) {
        messages.add(new Message(MessageType.SUCCESS, message));
    }

    public void addWarnMessage(String message) {
        messages.add(new Message(MessageType.WARN, message));
    }

    public void addErrorMessage(String message) {
        messages.add(new Message(MessageType.ERROR, message));
    }
}
