package ca.bigmwaj.emapp.as.service;

import ca.bigmwaj.emapp.as.entity.common.AbstractBaseEntity;
import ca.bigmwaj.emapp.as.mapper.AbstractMapper;

public abstract class AbstractBaseService extends AbstractMapper {

    protected <E extends AbstractBaseEntity> E retire(E e) {
        e.setRetired(true);
        return e;
    }
}
