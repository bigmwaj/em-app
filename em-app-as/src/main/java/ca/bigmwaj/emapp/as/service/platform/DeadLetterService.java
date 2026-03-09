package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.DeadLetterDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.DeadLetterDto;
import ca.bigmwaj.emapp.as.entity.platform.DeadLetterEntity;
import ca.bigmwaj.emapp.as.integration.KafkaPublisher;
import ca.bigmwaj.emapp.as.mapper.DeadLetterMapper;
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

    private final DeadLetterDao dao;

    private final KafkaPublisher kafkaPublisher;

    private final DeadLetterMapper mapper;

    @Autowired
    public DeadLetterService(DeadLetterDao dao, KafkaPublisher kafkaPublisher, DeadLetterMapper mapper) {
        this.dao = dao;
        this.kafkaPublisher = kafkaPublisher;
        this.mapper = mapper;
    }

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

    private void retry(DeadLetterDto dto) {
        String jsonMessage = dto.getMessage();
        ObjectNode jsonNode = insertDeadLetterId(jsonMessage, dto.getId());
        kafkaPublisher.publish(dto.getEventName(), jsonNode);
    }

    public DeadLetterDto update(DeadLetterDto dto) {
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(mapper.mappingForUpdate(dto)));
    }

    public DeadLetterDto create(DeadLetterDto dto) {
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(mapper.mappingForCreate(dto)));
    }

    public DeadLetterDto changeStatus(DeadLetterDto dto) {
        var entity = mapper.mappingForStatusChange(dto);
        dto = GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
        if (DeadLetterStatusLvo.RETRY.equals(dto.getStatus())) {
            retry(dto);
        }
        return dto;
    }

    protected DeadLetterDao getDao() {
        return dao;
    }

    protected Function<DeadLetterEntity, DeadLetterDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }
}
