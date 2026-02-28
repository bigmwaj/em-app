package ca.bigmwaj.emapp.ig.transform;

import ca.bigmwaj.emapp.dm.dto.platform.SharedDeadLetterDto;
import ca.bigmwaj.emapp.dm.lvo.platform.DeadLetterStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.shared.EditActionLvo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DeadLetterMapper {

    private final Logger logger = LoggerFactory.getLogger(DeadLetterMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long getDeadLetterId(String message) {
        Objects.requireNonNull(message, "message must not be null");
        logger.info("The initial message is {}", message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.has("dead-letter-id")) {
                return jsonNode.get("dead-letter-id").asLong();
            }else{
                logger.debug(jsonNode.toString());
                logger.warn("Message does not contain 'dead-letter-id': {}", message);
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public SharedDeadLetterDto map(String message) {
        var builder = SharedDeadLetterDto.builder()
                .withErrorMessage("Error processing message")
                .withMessage(message)
                .withStatus(DeadLetterStatusLvo.ERROR)
                .withEditAction(EditActionLvo.CREATE)
                .withEventName("user-created");

        Long deadLetterId = getDeadLetterId(message);
        if (deadLetterId != null) {
            builder.withEditAction(EditActionLvo.UPDATE)
                    .withId(deadLetterId);
        }

        return builder.build();
    }
}
