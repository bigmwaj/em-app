package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactEmailDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.ContactEmailDto;
import ca.bigmwaj.emapp.as.entity.platform.ContactEmailEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class ContactEmailService extends AbstractService {
    @Autowired
    private ContactEmailDao dao;

    @Autowired
    private ContactDao contactDao;

    private Example<ContactEmailEntity> getCommonCriteria(Long contactId) {
        var probe = new ContactEmailEntity();
        probe.setContact(new ContactEntity());
        probe.getContact().setId(contactId);
        return Example.of(probe);
    }

    public List<ContactEmailDto> findAll(Long contactId) {
        var example = getCommonCriteria(contactId);
        return dao.findAll(example).stream().map(GlobalMapper.INSTANCE::toDto).toList();
    }

    private Optional<ContactEmailEntity> findEntityById(Long contactId, Long emailId) {
        var example = getCommonCriteria(contactId);
        example.getProbe().setId(emailId);
        return dao.findAll(example).stream().findFirst();
    }

    public ContactEmailDto findById(Long contactId, Long emailId) {
        return findEntityById(contactId, emailId)
                .map(GlobalMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Contact email not found with contactId: " + contactId + " and emailId: " + emailId));
    }

    public void deleteById(Long contactId, Long emailId) {
        findEntityById(contactId, emailId).ifPresentOrElse(dao::delete, () -> {
            throw new NoSuchElementException("Contact email not found with contactId: " + contactId + " and emailId: " + emailId);
        });
    }

    public ContactEmailDto create(Long contactId, ContactEmailDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public ContactEmailDto update(Long contactId, ContactEmailDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public boolean isEmailUnique(ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo holderType, String email) {
        return !dao.existsByHolderTypeAndEmail(holderType, email);
    }
}
