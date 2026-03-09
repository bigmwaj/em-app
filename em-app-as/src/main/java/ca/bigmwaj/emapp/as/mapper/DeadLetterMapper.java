package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.DeadLetterDao;
import ca.bigmwaj.emapp.as.dto.platform.DeadLetterDto;
import ca.bigmwaj.emapp.as.entity.platform.DeadLetterEntity;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterMapper extends AbstractMapper {

    private final DeadLetterDao deadLetterDao;

    public DeadLetterMapper(DeadLetterDao deadLetterDao) {
        this.deadLetterDao = deadLetterDao;
    }

    public DeadLetterEntity mappingForCreate(DeadLetterDto dto) {
        var entity = new DeadLetterEntity();
        entity.setMessage(dto.getMessage());
        entity.setStatus(dto.getStatus());
        entity.setStatusReason(dto.getStatusReason());
        entity.setErrorMessage(dto.getErrorMessage());
        return beforeCreateHistEntity(entity);
    }

    public DeadLetterEntity mappingForUpdate(DeadLetterDto dto) {
        var entity = deadLetterDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("DeadLetter not found with id: " + dto.getId()));
        entity.setMessage(dto.getMessage());
        return beforeUpdateHistEntity(entity);
    }

    public DeadLetterEntity mappingForStatusChange(DeadLetterDto dto) {
        var entity = deadLetterDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("DeadLetter not found with id: " + dto.getId()));
        entity.setStatus(dto.getStatus());
        entity.setStatusReason(dto.getStatusReason());
        entity.setStatusDate(dto.getStatusDate());
        return entity;
    }
}
