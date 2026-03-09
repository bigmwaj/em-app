package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactPK;
import ca.bigmwaj.emapp.as.lvo.platform.AccountContactRoleLvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountContactDao extends JpaRepository<AccountContactEntity, AccountContactPK> {

    Optional<AccountContactEntity> findByAccountIdAndRole(Short id, AccountContactRoleLvo accountContactRoleLvo);
}
