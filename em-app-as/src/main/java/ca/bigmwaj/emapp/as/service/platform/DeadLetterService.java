package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.DeadLetterDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.DeadLetterDto;
import ca.bigmwaj.emapp.as.entity.platform.DeadLetterEntity;
import ca.bigmwaj.emapp.as.integration.KafkaPublisher;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import ca.bigmwaj.emapp.dm.lvo.platform.DeadLetterStatusLvo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class DeadLetterService extends AbstractMainService<DeadLetterDto, DeadLetterEntity, Long> {

    @Autowired
    private DeadLetterDao dao;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    private ObjectNode insertDeadLetterId(String message, Long id) {
        Objects.requireNonNull(message, "message must not be null");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(message);
            jsonNode.put("dead-letter-id", id);
            return jsonNode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    public DeadLetterDto update(DeadLetterDto dto) {
        if (DeadLetterStatusLvo.RETRY.equals(dto.getStatus())) {
            String jsonMessage = dto.getMessage();
            ObjectNode jsonNode = insertDeadLetterId(jsonMessage, dto.getId());
            dto.setMessage(jsonMessage);
            kafkaPublisher.publish(dto.getEventName(), jsonNode);
        }
        DeadLetterEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    public DeadLetterDto create(DeadLetterDto dto) {
        DeadLetterEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    protected DeadLetterDao getDao() {
        return dao;
    }

    protected Function<DeadLetterEntity, DeadLetterDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }
}
