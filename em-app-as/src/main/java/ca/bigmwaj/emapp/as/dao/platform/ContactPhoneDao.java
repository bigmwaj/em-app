package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.ContactPhoneEntity;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactPhoneDao extends JpaRepository<ContactPhoneEntity, Long> {

    List<ContactPhoneEntity> findAllByContactId(Long id);
    
    boolean existsByHolderTypeAndPhone(HolderTypeLvo holderType, String phone);
}
