package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.common.AbstractSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.DataListDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.mapper.AccountMapper;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class AccountService extends AbstractMainService<AccountDto, AccountEntity, Short> {

    private final AccountDao dao;

    private final AccountMapper mapper;

    private final UserService userService;

    private final ContactService contactService;

    private final UserDao userDao;

    @Autowired
    public AccountService(AccountDao dao, AccountMapper mapper, UserService userService, ContactService contactService, UserDao userDao) {
        this.dao = dao;
        this.mapper = mapper;
        this.userService = userService;
        this.contactService = contactService;
        this.userDao = userDao;
    }

    @Override
    protected Function<AccountEntity, AccountDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected AccountDao getDao() {
        return dao;
    }

    @Override
    public DataListDto<AccountDto> search(AbstractSearchCriteria sc) {
        return super.search(sc);
    }

    public AccountDto create(AccountDto dto) {
        var entity = mapper.mappingForCreate(dto);

        if (dto.getAccountContacts() != null && !dto.getAccountContacts().isEmpty()) {
            entity.setAccountContacts(new ArrayList<>(dto.getAccountContacts().size()));

            for (var accountContact : dto.getAccountContacts()) {
                var contact = accountContact.getContact();
                contact = contactService.create(contact);

                accountContact.setContact(contact);
                entity.getAccountContacts().add(mapper.mappingForCreate(entity, accountContact));
            }
        }
        entity = dao.save(entity);
        dto.setId(entity.getId());
        userService.createAccountAdminUser(dto);
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }

    private void removeDeletedContacts(AccountDto dto, AccountEntity entity) {
        var removed = dto.getAccountContacts().stream()
                .filter(AccountContactDto::isDeleteAction)
                .map(ac -> ac.getContact().getId())
                .toList();

        if (!removed.isEmpty()) {
            entity.getAccountContacts().removeIf(ac -> removed.contains(ac.getContact().getId()));
        }
    }

    private void addNewContacts(AccountDto dto, AccountEntity entity) {
        dto.getAccountContacts().stream()
                .filter(AccountContactDto::isCreateAction)
                .map(ac -> mapper.mappingForCreate(entity, ac))
                .forEach(entity.getAccountContacts()::add);
    }

    public AccountDto update(AccountDto dto) {
        var entity = mapper.mappingForUpdate(dto);
        if (dto.getAccountContacts() != null && !dto.getAccountContacts().isEmpty()) {
            removeDeletedContacts(dto, entity);
            addNewContacts(dto, entity);
        }
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    public AccountDto changeStatus(AccountDto dto) {
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(mapper.mappingForStatusChange(dto)));
    }

    private<E> List<E> emptyIfNull(List<E> list) {
        return list != null ? list : List.of();
    }

    private List<UserEntity> getAccountUsers(AccountEntity account) {
        var contacts = account.getAccountContacts().stream()
                .map(AccountContactEntity::getContact)
                .toList();
        return userDao.findAllByContactIn(contacts);
    }

    @Override
    public void deleteById(Short id) {
        var account = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));

        retire(account).getAccountContacts()
                .stream()
                .map(this::retire)
                .map(AccountContactEntity::getContact)
                .map(this::retire)
                .map(e -> List.of(emptyIfNull(e.getEmails()), emptyIfNull(e.getPhones()), emptyIfNull(e.getAddresses())))
                .flatMap(List::stream)
                .flatMap(List::stream)
                .forEach(this::retire);

        getAccountUsers(account).forEach(this::retire);

        dao.save(account);
    }
}
