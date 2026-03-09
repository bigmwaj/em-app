package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.ContactAddressDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactEmailDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactPhoneDao;
import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.entity.platform.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class ContactMapper extends AbstractMapper {

    private final ContactDao contactDao;
    private final ContactAddressDao contactAddressDao;
    private final ContactPhoneDao contactPhoneDao;
    private final ContactEmailDao contactEmailDao;

    public ContactMapper(ContactDao contactDao, ContactAddressDao contactAddressDao, ContactPhoneDao contactPhoneDao, ContactEmailDao contactEmailDao) {
        this.contactDao = contactDao;
        this.contactAddressDao = contactAddressDao;
        this.contactPhoneDao = contactPhoneDao;
        this.contactEmailDao = contactEmailDao;
    }

    public ContactEntity mappingForCreate(ContactDto dto) {
        var entity = new ContactEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setOwnerType(dto.getOwnerType());
        entity.setBirthDate(dto.getBirthDate());
        return beforeCreateHistEntity(entity);
    }

    public ContactEntity mappingForUpdate(ContactDto dto) {
        var entity = contactDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + dto.getId()));
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return beforeUpdateHistEntity(entity);
    }

    private <E extends AbstractContactPointEntity, D extends AbstractContactPointDto> E mappingForCreate(ContactEntity entity, D dto, E child) {
        child.setContact(entity);
        child.setDefaultContactPoint(dto.getDefaultContactPoint());
        child.setOwnerType(dto.getOwnerType());
        return beforeCreateHistEntity(child);
    }

    private <E extends AbstractContactPointEntity, D extends AbstractContactPointDto> E
    findChild(ContactEntity entity, D dto, Function<Long, Optional<E>> findFunction) {
        return findFunction.apply(dto.getId())
                .filter(e -> e.getContact().getId().equals(entity.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + dto.getId()));
    }

    public ContactAddressEntity mappingForCreate(ContactEntity entity, ContactAddressDto dto) {
        var child = mappingForCreate(entity, dto, new ContactAddressEntity());
        child.setType(dto.getType());
        child.setCity(dto.getCity());
        child.setAddress(dto.getAddress());
        child.setCountry(dto.getCountry());
        child.setRegion(dto.getRegion());
        return child;
    }

    public ContactAddressEntity mappingForUpdate(ContactEntity entity, ContactAddressDto dto) {
        var child = findChild(entity, dto, contactAddressDao::findById);
        child.setCity(dto.getCity());
        child.setType(dto.getType());
        child.setAddress(dto.getAddress());
        child.setCountry(dto.getCountry());
        child.setRegion(dto.getRegion());
        child.setDefaultContactPoint(dto.getDefaultContactPoint());
        return beforeUpdateHistEntity(child);
    }

    public ContactPhoneEntity mappingForCreate(ContactEntity entity, ContactPhoneDto dto) {
        var child = mappingForCreate(entity, dto, new ContactPhoneEntity());
        child.setType(dto.getType());
        child.setExtension(dto.getExtension());
        child.setIndicative(dto.getIndicative());
        child.setPhone(dto.getPhone());
        return child;
    }

    public ContactPhoneEntity mappingForUpdate(ContactEntity entity, ContactPhoneDto dto) {
        var child = findChild(entity, dto, contactPhoneDao::findById);
        child.setDefaultContactPoint(dto.getDefaultContactPoint());
        child.setType(dto.getType());
        child.setExtension(dto.getExtension());
        child.setIndicative(dto.getIndicative());
        child.setPhone(dto.getPhone());
        return beforeUpdateHistEntity(child);
    }

    public ContactEmailEntity mappingForCreate(ContactEntity entity, ContactEmailDto dto) {
        var child = mappingForCreate(entity, dto, new ContactEmailEntity());
        child.setType(dto.getType());
        child.setEmail(dto.getEmail());
        return child;
    }

    public ContactEmailEntity mappingForUpdate(ContactEntity entity, ContactEmailDto dto) {
        var child = findChild(entity, dto, contactEmailDao::findById);
        child.setDefaultContactPoint(dto.getDefaultContactPoint());
        child.setType(dto.getType());
        child.setEmail(dto.getEmail());
        return beforeUpdateHistEntity(child);
    }
}
