package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.GroupRoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupRolePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRoleDao extends JpaRepository<GroupRoleEntity, GroupRolePK> {

}
