package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactPK;
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountContactDao extends JpaRepository<AccountContactEntity, AccountContactPK> {

    List<AccountContactEntity> findAllByAccountId(Long id);

    Optional<AccountContactEntity> findAtMostOneByAccountIdAndRole(Long id, AccountContactRoleLvo accountContactRoleLvo);
}
