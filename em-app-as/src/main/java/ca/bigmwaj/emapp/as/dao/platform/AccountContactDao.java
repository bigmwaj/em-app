package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountContactDao extends JpaRepository<AccountContactEntity, AccountContactPK> {

}
