package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.entity.common.AbstractChangeTrackingEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class AbstractMapper {

    protected String getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Authentication object is null");
        }

        return authentication.getName();
    }

    protected <T extends AbstractBaseEntity> T beforeCreateHistEntity(T entity) {
        if (entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setCreatedBy(getCurrentUser());
            ce.setCreatedDate(LocalDateTime.now());
            ce.setUpdatedBy(getCurrentUser());
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }

    protected <T extends AbstractBaseEntity> T beforeUpdateHistEntity(T entity) {
        if (entity instanceof AbstractChangeTrackingEntity ce) {
            ce.setUpdatedBy(getCurrentUser());
            ce.setUpdatedDate(LocalDateTime.now());
        }
        return entity;
    }
}
