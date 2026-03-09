package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.AbstractContactPointDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
import ca.bigmwaj.emapp.as.mapper.ContactMapper;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class ContactService extends AbstractMainService<ContactDto, ContactEntity, Long> {

    private final ContactDao dao;

    private final ContactMapper mapper;

    @Autowired
    public ContactService(ContactDao dao, ContactMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Override
    protected Function<ContactEntity, ContactDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected ContactDao getDao() {
        return dao;
    }

    public ContactDto create(ContactDto dto) {
        var entity = mapper.mappingForCreate(dto);
        if (dto.getEmails() != null && !dto.getEmails().isEmpty()) {
            var emails = dto.getEmails().stream()
                    .map(e -> mapper.mappingForCreate(entity, e))
                    .toList();
            entity.setEmails(emails);
        }

        if (dto.getPhones() != null && !dto.getPhones().isEmpty()) {
            var phones = dto.getPhones().stream()
                    .map(p -> mapper.mappingForCreate(entity, p))
                    .toList();
            entity.setPhones(phones);
        }

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            var addresses = dto.getAddresses().stream()
                    .map(a -> mapper.mappingForCreate(entity, a))
                    .toList();
            entity.setAddresses(addresses);
        }
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    private void removedDeleted(List<? extends AbstractContactPointDto> dtoList, List<? extends AbstractContactPointEntity> entities) {
        var removed = dtoList.stream()
                .filter(AbstractContactPointDto::isDeleteAction)
                .map(AbstractContactPointDto::getId)
                .toList();
        entities.removeIf(e -> removed.contains(e.getId()));
    }

    private void addNewEmails(ContactDto dto, ContactEntity entity) {
        var newEmails = dto.getEmails().stream()
                .filter(AbstractContactPointDto::isCreateAction)
                .map(e -> mapper.mappingForCreate(entity, e))
                .toList();
        entity.getEmails().addAll(newEmails);
    }

    private void addNewPhones(ContactDto dto, ContactEntity entity) {
        var newPhones = dto.getPhones().stream()
                .filter(AbstractContactPointDto::isCreateAction)
                .map(p -> mapper.mappingForCreate(entity, p))
                .toList();
        entity.getPhones().addAll(newPhones);
    }

    private void addNewAddresses(ContactDto dto, ContactEntity entity) {
        var newAddresses = dto.getAddresses().stream()
                .filter(AbstractContactPointDto::isCreateAction)
                .map(a -> mapper.mappingForCreate(entity, a))
                .toList();
        entity.getAddresses().addAll(newAddresses);
    }

    private void updateChangedEmails(ContactDto dto, ContactEntity entity) {
        var changedEmails = dto.getEmails().stream()
                .filter(AbstractContactPointDto::isUpdateAction)
                .map(e -> mapper.mappingForUpdate(entity, e))
                .toList();

        var changedPhoneIds = changedEmails.stream().map(ContactEmailEntity::getId).toList();
        entity.getEmails().removeIf(e -> changedPhoneIds.contains(e.getId()));
        entity.getEmails().addAll(changedEmails);
    }

    private void updateChangedPhones(ContactDto dto, ContactEntity entity) {
        var changedPhones = dto.getPhones().stream()
                .filter(AbstractContactPointDto::isUpdateAction)
                .map(p -> mapper.mappingForUpdate(entity, p))
                .toList();

        var changedPhoneIds = changedPhones.stream().map(ContactPhoneEntity::getId).toList();
        entity.getPhones().removeIf(p -> changedPhoneIds.contains(p.getId()));
        entity.getPhones().addAll(changedPhones);
    }

    private void updateChangedAddresses(ContactDto dto, ContactEntity entity) {
        var changedAddresses = dto.getAddresses().stream()
                .filter(AbstractContactPointDto::isUpdateAction)
                .map(a -> mapper.mappingForUpdate(entity, a))
                .toList();

        var changedAddressIds = changedAddresses.stream()
                .map(ContactAddressEntity::getId)
                .toList();

        entity.getAddresses().removeIf(a -> changedAddressIds.contains(a.getId()));
        entity.getAddresses().addAll(changedAddresses);
    }

    public ContactDto update(ContactDto dto) {
        var entity = mapper.mappingForUpdate(dto);
        if (dto.getEmails() != null && !dto.getEmails().isEmpty()) {
            removedDeleted(dto.getEmails(), entity.getEmails());
            addNewEmails(dto, entity);
            updateChangedEmails(dto, entity);
        }

        if (dto.getPhones() != null && !dto.getPhones().isEmpty()) {
            removedDeleted(dto.getPhones(), entity.getPhones());
            addNewPhones(dto, entity);
            updateChangedPhones(dto, entity);
        }

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            removedDeleted(dto.getAddresses(), entity.getAddresses());
            addNewAddresses(dto, entity);
            updateChangedAddresses(dto, entity);
        }
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

}
