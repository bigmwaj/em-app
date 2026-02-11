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
import ca.bigmwaj.emapp.as.entity.platform.*;
import ca.bigmwaj.emapp.as.service.AbstractService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
        initCreateContactPoints(entity, entity.getEmails());
        initCreateContactPoints(entity, entity.getPhones());
        initCreateContactPoints(entity, entity.getAddresses());
        entity = dao.save(entity);
        return findById(entity.getId());
    }

    public ContactDto update(ContactDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        initUpdateContactPoints(entity, entity.getEmails(), dto.getEmails(), this::deleteEmailIfApplicable);
        initUpdateContactPoints(entity, entity.getPhones(), dto.getPhones(), this::deletePhoneIfApplicable);
        initUpdateContactPoints(entity, entity.getAddresses(), dto.getAddresses(), this::deleteAddressIfApplicable);
        entity = dao.save(entity);
        return findById(entity.getId());
    }

    private <T extends AbstractContactPointEntity>
    void initCreateContactPoints(ContactEntity entity, List<T> contactPoints) {
        if (contactPoints != null) {
            UnaryOperator<T> setContact = e -> {
                e.setContact(entity);
                return e;
            };
            contactPoints.stream()
                    .map(setContact)
                    .forEach(this::beforeCreateHistEntity);
        }
    }

    private<D extends AbstractContactPointDto, T extends AbstractContactPointEntity>
    void initUpdateContactPoints(ContactEntity entity, List<T> contactPoints, List<D> contactPointDtos, Consumer<D> deleteFunction) {
        if (contactPoints != null) {
            final List<Long> toDelete;
            // Review this method and exclude deleted contact points that do not have an ID
            // (i.e. new contact points that are added and marked for deletion in the same update)
            if( contactPointDtos != null ){
                toDelete = contactPointDtos.stream()
                        .filter(AbstractContactPointDto::isToDelete)
                        .peek(deleteFunction)
                        .map(AbstractContactPointDto::getId)
                        .filter(Objects::nonNull)
                        .toList();
            } else {
                toDelete = Collections.emptyList();
            }

            UnaryOperator<T> setContact = e -> {
                e.setContact(entity);
                return e;
            };

            Predicate<T> isNotToDelete = e -> toDelete.isEmpty() || !toDelete.contains(e.getId());

            contactPoints.stream()
                    .filter(isNotToDelete)
                    .map(setContact)
                    .forEach(this::beforeUpdateHistEntity);
        }
    }

    private void deleteEmailIfApplicable(ContactEmailDto email) {
        if (email.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(email);
            emailDao.delete(entity);
        }
    }

    private void deletePhoneIfApplicable(ContactPhoneDto phone) {
        if (phone.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(phone);
            phoneDao.delete(entity);
        }
    }

    private void deleteAddressIfApplicable(ContactAddressDto address) {
        if (address.isToDelete()) {
            var entity = GlobalMapper.INSTANCE.toEntity(address);
            addressDao.delete(entity);
        }
    }

    /**
     * Performance optimization: Maps entity to DTO and includes children collections.
     * The ContactEntity now has @OneToMany relationships with SUBSELECT fetch mode,
     * which loads all children efficiently for all contacts in the result set.
     * This eliminates the N+1 query problem where each contact would trigger 3 additional queries.
     */
    protected ContactDto toDtoWithChildren(ContactEntity entity) {
        var dto = GlobalMapper.INSTANCE.toDto(entity);
        
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
