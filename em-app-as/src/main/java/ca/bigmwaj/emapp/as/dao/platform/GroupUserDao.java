package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.dao.AbstractDao;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserEntity;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupUserDao extends JpaRepository<GroupUserEntity, GroupUserPK> {

}
