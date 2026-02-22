package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.PrivilegeSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.UserSearchCriteria;
import ca.bigmwaj.emapp.as.entity.platform.PrivilegeEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeDao extends AbstractDao<PrivilegeEntity, Short> {

    default Class<PrivilegeEntity> getEntityClass() {
        return PrivilegeEntity.class;
    }

    @Override
    default String getSpecialWhereClause(AbstractSearchCriteria sc) {
        if (sc instanceof PrivilegeSearchCriteria usc && null != usc.getAssignableToRoleId()) {
            return "not exists (select 1 from RolePrivilegeEntity rp where rp.privilege = qRoot and rp.role.id = %d)".formatted(usc.getAssignableToRoleId());
        }
        return AbstractDao.super.getSpecialWhereClause(sc);
    }
}
