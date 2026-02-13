package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactPhoneDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.ContactPhoneDto;
import ca.bigmwaj.emapp.as.entity.platform.ContactPhoneEntity;
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
public class ContactPhoneService extends AbstractService {
    @Autowired
    private ContactPhoneDao dao;

    @Autowired
    private ContactDao contactDao;

    private Example<ContactPhoneEntity> getCommonCriteria(Long contactId) {
        var probe = new ContactPhoneEntity();
        probe.setContact(new ContactEntity());
        probe.getContact().setId(contactId);
        return Example.of(probe);
    }

    public List<ContactPhoneDto> findAll(Long contactId) {
        var example = getCommonCriteria(contactId);
        return dao.findAll(example).stream().map(GlobalMapper.INSTANCE::toDto).toList();
    }

    private Optional<ContactPhoneEntity> findEntityById(Long contactId, Long phoneId) {
        var example = getCommonCriteria(contactId);
        example.getProbe().setId(phoneId);
        return dao.findAll(example).stream().findFirst();
    }

    public ContactPhoneDto findById(Long contactId, Long phoneId) {
        return findEntityById(contactId, phoneId)
                .map(GlobalMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Contact phone not found with contactId: " + contactId + " and phoneId: " + phoneId));
    }

    public void deleteById(Long contactId, Long phoneId) {
        findEntityById(contactId, phoneId).ifPresentOrElse(dao::delete, () -> {
            throw new NoSuchElementException("Contact phone not found with contactId: " + contactId + " and phoneId: " + phoneId);
        });
    }

    public ContactPhoneDto create(Long contactId, ContactPhoneDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public ContactPhoneDto update(Long contactId, ContactPhoneDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public boolean isPhoneUnique(ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo holderType, String phone) {
        return !dao.existsByHolderTypeAndPhone(holderType, phone);
    }

    public void beforeCreate(ContactPhoneEntity entity, ContactPhoneDto dto) {
        beforeCreateHistEntity(entity);
    }
}
