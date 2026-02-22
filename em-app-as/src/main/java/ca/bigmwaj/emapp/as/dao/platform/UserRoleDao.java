package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserRolePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleDao extends JpaRepository<UserRoleEntity, UserRolePK> {

    List<UserRoleEntity> findByRoleId(Short roleId);

}
