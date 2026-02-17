package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;

import java.time.LocalDateTime;

public class AbstractService {

    protected static final String SYSTEM_USER = "IA";

    protected <E extends AbstractBaseEntity> E beforeCreateHistEntity(E entity) {
        if( entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setCreatedBy(SYSTEM_USER);
            ce.setCreatedDate(LocalDateTime.now());
            ce.setUpdatedBy(SYSTEM_USER);
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }

    protected <E extends AbstractBaseEntity> E beforeUpdateHistEntity(E entity) {
        if( entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setUpdatedBy(SYSTEM_USER);
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }
}
