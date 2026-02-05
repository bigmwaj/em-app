package ca.bigmwaj.emapp.as.dao.platform;

import ca.bigmwaj.emapp.as.entity.platform.ContactEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactEmailDao extends JpaRepository<ContactEmailEntity, Long> {
    List<ContactEmailEntity> findAllByContactId(Long id);
}
