package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserPK;
import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupUserDao extends JpaRepository<GroupUserEntity, GroupUserPK> {

    List<GroupUserEntity> findByGroupId(Short groupId);

}
