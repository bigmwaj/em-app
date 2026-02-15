package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class AccountService extends AbstractService {

    @Autowired
    private AccountDao dao;

    @Autowired
    private AccountContactService accountContactService;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    protected SearchResultDto<AccountDto> searchAll() {
        var r = dao.findAll().stream()
                .map(this::toDtoWithChildren)
                .toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<AccountDto> search(AccountSearchCriteria sc) {
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

    public AccountDto findById(Short accountId) {
        return dao.findById(accountId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException("Account not found with id: " + accountId));
    }

    public void deleteById(Short accountId) {
        dao.deleteById(accountId);
    }

    public AccountDto create(AccountDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreate(entity, dto);
        entity = dao.save(entity);
        userService.create(entity, dto.getAdminUsername(), dto.getAdminUsernameType());
        return GlobalMapper.INSTANCE.toDto(entity);
    }

    public void beforeCreate(AccountEntity entity, AccountDto dto) {
        beforeCreateHistEntity(entity);

        var accountContacts = entity.getAccountContacts();
        if( accountContacts == null || accountContacts.isEmpty()){
            return;
        }

        for(var accountContact : accountContacts) {
            if( accountContact.getAccount() != entity ){ // instance check
                accountContact.setAccount(entity);
            }
            beforeCreateHistEntity(accountContact);

            var contactToCreate = accountContact.getContact();
            if (contactToCreate != null) {
                accountContactService.beforeCreate(accountContact, null);
            }
        }
    }

    public AccountDto update(AccountDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    private AccountDto toDtoWithChildren(AccountEntity entity) {
        var dto = GlobalMapper.INSTANCE.toDto(entity);

        dto.setAccountContacts(entity.getAccountContacts().stream()
                .map(accountContactService::toDtoWithChildren)
                .toList());

        return dto;
    }
}
