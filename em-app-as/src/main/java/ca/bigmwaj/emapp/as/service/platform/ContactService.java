package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactAddressDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactEmailDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactPhoneDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.*;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.ContactAddressEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEmailEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactPhoneEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class ContactService extends AbstractService {

    @Autowired
    private ContactDao dao;

    @Autowired
    private ContactEmailDao emailDao;

    @Autowired
    private ContactPhoneDao phoneDao;

    @Autowired
    private ContactAddressDao addressDao;

    @PersistenceContext
    private EntityManager entityManager;

    protected SearchResultDto<ContactDto> searchAll() {
        var r = dao.findAll().stream()
                .map(this::toDtoWithChildren)
                .toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<ContactDto> search(DefaultSearchCriteria sc) {
        if (sc == null) {
            return searchAll();
        }

        var searchStats = new SearchInfos(sc);

        if (sc.isCalculateStatTotal()) {
            var total = dao.countAllByCriteria(entityManager, sc);
            searchStats.setTotal(total);
        }
        var r = dao.findAllByCriteria(entityManager, sc)
                .stream()
                .map(this::toDtoWithChildren)
                .toList();

        return new SearchResultDto<>(searchStats, r);
    }

    public ContactDto findById(Long contactId) {
        return dao.findById(contactId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException("Contact not found with id: " + contactId));
    }

    public void deleteById(Long contactId) {
        dao.deleteById(contactId);
    }

    public ContactDto create(ContactDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        entity = dao.save(entity);
        createEmails(dto, entity);
        createPhones(dto, entity);
        createAddresses(dto, entity);
        return findById(entity.getId());
    }

    public ContactDto update(ContactDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        updateEmails(dto, entity);
        updatePhones(dto, entity);
        updateAddresses(dto, entity);
        entity = dao.save(entity);
        return findById(entity.getId());
    }

    private void createEmails(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getEmails() != null) {
            UnaryOperator<ContactEmailEntity> setContact = e -> {
                e.setContact(contactEntity);
                return e;
            };
            dto.getEmails().stream()
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeCreateHistEntity)
                    .map(setContact)
                    .forEach(emailDao::save);
        }
    }

    private void createPhones(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getPhones() != null) {
            UnaryOperator<ContactPhoneEntity> setContact = e -> {
                e.setContact(contactEntity);
                return e;
            };
            dto.getPhones().stream()
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeCreateHistEntity)
                    .map(setContact)
                    .forEach(phoneDao::save);
        }
    }

    private void createAddresses(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getAddresses() != null) {
            UnaryOperator<ContactAddressEntity> setContact = e -> {
                e.setContact(contactEntity);
                return e;
            };
            dto.getAddresses().stream()
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeCreateHistEntity)
                    .map(setContact)
                    .forEach(addressDao::save);
        }
    }

    private void deleteEmailIfApplicable(ContactEmailDto email) {
        if (email.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(email);
            emailDao.delete(entity);
        }
    }

    private void updateEmails(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getEmails() != null) {
            dto.getEmails().stream()
                    .peek(this::deleteEmailIfApplicable)
                    .filter(ContactEmailDto::isNotToDelete)
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeUpdateHistEntity)
                    .forEach(emailDao::save);
        }
    }

    private void deletePhoneIfApplicable(ContactPhoneDto phone) {
        if (phone.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(phone);
            phoneDao.delete(entity);
        }
    }

    private void updatePhones(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getPhones() != null) {
            dto.getPhones().stream()
                    .peek(this::deletePhoneIfApplicable)
                    .filter(ContactPhoneDto::isNotToDelete)
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeUpdateHistEntity)
                    .forEach(phoneDao::save);
        }
    }

    private void deleteAddressIfApplicable(ContactAddressDto address) {
        if (address.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(address);
            addressDao.delete(entity);
        }
    }

    private void updateAddresses(ContactDto dto, ContactEntity contactEntity) {
        if (dto.getAddresses() != null) {
            dto.getAddresses().stream()
                    .peek(this::deleteAddressIfApplicable)
                    .filter(ContactAddressDto::isNotToDelete)
                    .map(GlobalMapper.INSTANCE::toEntity)
                    .map(this::beforeUpdateHistEntity)
                    .forEach(addressDao::save);
        }
    }

    /**
     * Performance optimization: Maps entity to DTO and includes children collections.
     * The ContactEntity now has @OneToMany relationships with SUBSELECT fetch mode,
     * which loads all children efficiently for all contacts in the result set.
     * This eliminates the N+1 query problem where each contact would trigger 3 additional queries.
     */
    protected ContactDto toDtoWithChildren(ContactEntity entity) {
        ContactDto dto = GlobalMapper.INSTANCE.toDto(entity);
        
        // Map child collections directly from the entity's pre-loaded collections
        dto.setEmails(entity.getEmails().stream()
                .map(GlobalMapper.INSTANCE::toDto)
                .toList());

        if( dto.getEmails() != null && !dto.getEmails().isEmpty() ){
            dto.setMainEmail(dto.getEmails().get(0));
        }
        
        dto.setPhones(entity.getPhones().stream()
                .map(GlobalMapper.INSTANCE::toDto)
                .toList());

        if( dto.getPhones() != null && !dto.getPhones().isEmpty() ){
            dto.setMainPhone(dto.getPhones().get(0));
        }
        
        dto.setAddresses(entity.getAddresses().stream()
                .map(GlobalMapper.INSTANCE::toDto)
                .toList());

        if( dto.getAddresses() != null && !dto.getAddresses().isEmpty() ){
            dto.setMainAddress(dto.getAddresses().get(0));
        }
        
        return dto;
    }
}
