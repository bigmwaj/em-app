package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.AbstractContactPointDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.entity.platform.*;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class ContactService extends AbstractMainService<ContactDto, ContactEntity, Long> {

    @Autowired
    private ContactDao dao;

    @Autowired
    private ContactEmailService emailService;

    @Autowired
    private ContactPhoneService phoneService;

    @Autowired
    private ContactAddressService addressService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Function<ContactEntity, ContactDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected ContactDao getDao() {
        return dao;
    }

    public ContactDto findById(Long contactId) {
        String errorMessage = "Contact not found with id: ";
        return dao.findById(contactId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException(errorMessage + contactId));
    }

    public void deleteById(Long contactId) {
        dao.deleteById(contactId);
    }

    public ContactEntity createAndReturnEntity(ContactDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeCreate(entity, dto);
        return dao.save(entity);
    }

    public ContactDto create(ContactDto dto) {
        var entity = createAndReturnEntity(dto);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }

    public ContactDto update(ContactDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeUpdate(entity, dto);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    /**
     * Performance optimization: Maps entity to DTO and includes children collections.
     * The ContactEntity now has @OneToMany relationships with SUBSELECT fetch mode,
     * which loads all children efficiently for all contacts in the result set.
     * This eliminates the N+1 query problem where each contact would trigger 3 additional queries.
     */
    protected ContactDto toDtoWithChildren(ContactEntity entity) {
        var dto = GlobalPlatformMapper.INSTANCE.toDto(entity);

        // Map child collections directly from the entity's pre-loaded collections
        // Handle null collections defensively
        if (entity.getEmails() != null) {
            dto.setEmails(entity.getEmails().stream()
                    .map(GlobalPlatformMapper.INSTANCE::toDto)
                    .toList());
        }

        if (entity.getPhones() != null) {
            dto.setPhones(entity.getPhones().stream()
                    .map(GlobalPlatformMapper.INSTANCE::toDto)
                    .toList());
        }

        if (entity.getAddresses() != null) {
            dto.setAddresses(entity.getAddresses().stream()
                    .map(GlobalPlatformMapper.INSTANCE::toDto)
                    .toList());
        }

        return dto;
    }

    void beforeCreate(ContactEntity entity, ContactDto dto) {
        beforeCreateHistEntity(entity);
        entity.setId(null);

        List<ContactEmailEntity> emails = entity.getEmails();
        if (emails != null && !emails.isEmpty()) {
            for (ContactEmailEntity email : emails) {
                email.setContact(entity);
                emailService.beforeCreate(email, null);
            }
        }

        List<ContactPhoneEntity> phones = entity.getPhones();
        if (phones != null && !phones.isEmpty()) {
            for (ContactPhoneEntity phone : phones) {
                phone.setContact(entity);
                phoneService.beforeCreate(phone, null);
            }
        }

        List<ContactAddressEntity> addresses = entity.getAddresses();
        if (addresses != null && !addresses.isEmpty()) {
            for (ContactAddressEntity address : addresses) {
                address.setContact(entity);
                addressService.beforeCreate(address, null);
            }
        }
    }

    void beforeUpdate(ContactEntity entity, ContactDto dto) {
        String contactIdNullError = "Contact must be non-null for update";
        String contactDtoNullError = "Contact DTO must be provided for update";

        Objects.requireNonNull(entity.getId(), contactIdNullError);
        Objects.requireNonNull(dto, contactDtoNullError);

        beforeUpdateHistEntity(entity);

        var emails = entity.getEmails();
        if (emails != null && !emails.isEmpty()) {
            var finals = synContactPoints(entity, entity.getEmails(), dto.getEmails(), emailService);
            entity.setEmails(finals);
        }

        var phones = entity.getPhones();
        if (phones != null && !phones.isEmpty()) {
            var finals = synContactPoints(entity, entity.getPhones(), dto.getPhones(), phoneService);
            entity.setPhones(finals);
        }

        var addresses = entity.getAddresses();
        if (addresses != null && !addresses.isEmpty()) {
            var finals = synContactPoints(entity, entity.getAddresses(), dto.getAddresses(), addressService);
            entity.setAddresses(finals);
        }
    }

    private <E extends AbstractContactPointEntity, D extends AbstractContactPointDto> List<E> synContactPoints(
            ContactEntity entity,
            List<E> contactPoints,
            List<D> contactPointDtoLists,
            AbstractContactPointService<D, E> service) {

        String contactPointDtoNullError = "Contact Point DTO list must be provided for update " +
                "when contact has existing contact points";
        String contactPointSizeError = "Contact Point DTO list size must be equal to the existing " +
                "Contact Point entities list size for update";
        String editActionNullError = "Contact Point DTO Edit Action must be provided for update";

        Objects.requireNonNull(contactPointDtoLists, contactPointDtoNullError);
        List<E> finalContactPoints = new ArrayList<>();
        int total = contactPoints.size();
        if (contactPointDtoLists.size() != total) {
            throw new IllegalArgumentException(contactPointSizeError);
        }

        for (int i = 0; i < total; i++) {
            E e = contactPoints.get(i);
            D d = contactPointDtoLists.get(i);
            e.setContact(entity);
            Objects.requireNonNull(d.getEditAction(), editActionNullError);
            switch (d.getEditAction()) {
                case CREATE:
                    service.beforeCreate(e, d);
                    finalContactPoints.add(e);
                    break;

                case UPDATE:
                    finalContactPoints.add(service.beforeUpdate(e, d));
                    break;

                case DELETE:
                    if (!d.isNew()) {
                        service.delete(e, d);
                    }
                    break;
            }
        }
        return finalContactPoints;
    }
}
