package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.GroupRoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupRolePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRoleDao extends JpaRepository<GroupRoleEntity, GroupRolePK> {

    List<GroupRoleEntity> findByGroupId(Short groupId);
}
