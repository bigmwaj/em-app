package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactAddressDao extends JpaRepository<ContactAddressEntity, Long> {

    List<ContactAddressEntity> findAllByContactId(Long id);
    
    boolean existsByHolderTypeAndAddress(HolderTypeLvo holderType, String address);
}
