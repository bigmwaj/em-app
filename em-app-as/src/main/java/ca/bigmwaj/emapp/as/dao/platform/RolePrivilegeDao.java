package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePrivilegeDao extends JpaRepository<RolePrivilegeEntity, RolePrivilegePK> {

    List<RolePrivilegeEntity> findByRoleId(Short roleId);

}
