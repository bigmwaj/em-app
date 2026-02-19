package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactAddressDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.ContactAddressDto;
import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class ContactAddressService extends AbstractContactPointService<ContactAddressEntity, ContactAddressDto> {

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
        return dao.findAll(example).stream().map(GlobalPlatformMapper.INSTANCE::toDto).toList();
    }

    private Optional<ContactAddressEntity> findEntityById(Long contactId, Long addressId) {
        var example = getCommonCriteria(contactId);
        example.getProbe().setId(addressId);
        return dao.findAll(example).stream().findFirst();
    }

    public ContactAddressDto findById(Long contactId, Long addressId) {
        return findEntityById(contactId, addressId)
                .map(GlobalPlatformMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException("Contact address not found with contactId: " + contactId + " and addressId: " + addressId));
    }

    public void deleteById(Long contactId, Long addressId) {
        findEntityById(contactId, addressId).ifPresentOrElse(dao::delete, () -> {
            throw new NoSuchElementException("Contact address not found with contactId: " + contactId + " and addressId: " + addressId);
        });
    }

    @Override
    ContactAddressDao getDao() {
        return dao;
    }
}
