package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;

import java.time.LocalDateTime;

public class AbstractService {

    protected static final String SYSTEM_USER = "IA";

    protected <E extends AbstractBaseEntity> E beforeCreateHistEntity(E entity) {
        entity.setCreatedBy(SYSTEM_USER);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedBy(SYSTEM_USER);
        entity.setUpdatedDate(LocalDateTime.now());

        return entity;
    }

    protected <E extends AbstractBaseEntity> E beforeUpdateHistEntity(E entity) {
        entity.setUpdatedBy(SYSTEM_USER);
        entity.setUpdatedDate(LocalDateTime.now());

        return entity;
    }
}
