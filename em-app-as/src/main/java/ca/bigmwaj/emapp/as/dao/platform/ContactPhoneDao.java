package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.ContactPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPhoneDao extends JpaRepository<ContactPhoneEntity, Long> {

}
