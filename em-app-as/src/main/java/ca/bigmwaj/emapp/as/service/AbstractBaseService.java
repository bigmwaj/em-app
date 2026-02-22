package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import ca.bigmwaj.emapp.dm.dto.AbstractBaseDto;

import java.time.LocalDateTime;

public abstract class AbstractBaseService<D extends AbstractBaseDto, E extends AbstractBaseEntity> {

    protected static final String SYSTEM_USER = "IA";

    protected <T extends AbstractBaseEntity> T beforeCreateHistEntity(T entity) {
        if( entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setCreatedBy(SYSTEM_USER);
            ce.setCreatedDate(LocalDateTime.now());
            ce.setUpdatedBy(SYSTEM_USER);
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }

    protected <T extends AbstractBaseEntity> T beforeUpdateHistEntity(T entity) {
        if( entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setUpdatedBy(SYSTEM_USER);
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }
}
