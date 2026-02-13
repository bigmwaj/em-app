package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactAddressDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
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
public class ContactAddressService extends AbstractService {

    @Autowired
    private ContactAddressDao dao;

    @Autowired
    private ContactDao contactDao;

    private Example<ContactAddressEntity> getCommonCriteria(Long contactId) {
        var probe = new ContactAddressEntity();
        probe.setContact(new ContactEntity());
        probe.getContact().setId(contactId);
        return Example.of(probe);
    }

    public List<ContactAddressDto> findAll(Long contactId) {
        var example = getCommonCriteria(contactId);
        return dao.findAll(example).stream().map(GlobalMapper.INSTANCE::toDto).toList();
    }

    private Optional<ContactAddressEntity> findEntityById(Long contactId, Long addressId) {
        var example = getCommonCriteria(contactId);
        example.getProbe().setId(addressId);
        return dao.findAll(example).stream().findFirst();
    }

    public ContactAddressDto findById(Long contactId, Long addressId) {
        return findEntityById(contactId, addressId)
                .map(GlobalMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Contact address not found with contactId: " + contactId + " and addressId: " + addressId));
    }

    public void deleteById(Long contactId, Long addressId) {
        findEntityById(contactId, addressId).ifPresentOrElse(dao::delete, () -> {
            throw new NoSuchElementException("Contact address not found with contactId: " + contactId + " and addressId: " + addressId);
        });
    }

    public ContactAddressDto create(Long contactId, ContactAddressDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public ContactAddressDto update(Long contactId, ContactAddressDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        var contactEntity = contactDao.findById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
        entity.setContact(contactEntity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public boolean isAddressUnique(ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo holderType, String address) {
        return !dao.existsByHolderTypeAndAddress(holderType, address);
    }

    public void prepareCreation(ContactAddressEntity entity, ContactAddressDto dto) {
        beforeCreateHistEntity(entity);
    }
}
