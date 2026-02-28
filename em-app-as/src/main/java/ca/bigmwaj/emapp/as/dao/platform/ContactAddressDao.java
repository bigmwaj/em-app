package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactAddressDao extends JpaRepository<ContactAddressEntity, Long> {

}
