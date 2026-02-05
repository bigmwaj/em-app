package ca.bigmwaj.emapp.as.api.shared;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private MessageType type;

    private String message;
}
