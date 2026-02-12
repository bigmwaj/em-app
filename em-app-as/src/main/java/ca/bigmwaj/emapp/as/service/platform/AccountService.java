package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountSearchCriteria;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
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
    private AccountContactDao accountContactDao;

    @Autowired
    private AccountContactService accountContactService;

    @Autowired
    private ContactService contactService;

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

    public AccountDto findById(Long accountId) {
        return dao.findById(accountId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException("Account not found with id: " + accountId));
    }

    public void deleteById(Long accountId) {
        dao.deleteById(accountId);
    }

    public AccountDto create(AccountDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeCreateHistEntity(entity);
        entity = dao.save(entity);
        createAccountContact(entity, dto);
        return GlobalMapper.INSTANCE.toDto(entity);
    }

    public AccountDto update(AccountDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    private void createAccountContact(AccountEntity entity, AccountDto dto) {
        if (dto.getAccountContacts() != null) {
            for (var acDto : dto.getAccountContacts()) {
                acDto.setAccountId(entity.getId());
                accountContactService.create(acDto);
            }
        }
    }

    /**
     * Performance optimization: Maps entity to DTO and includes account contacts.
     * The AccountEntity now has @OneToMany relationship with SUBSELECT fetch mode,
     * which loads all account contacts efficiently. This eliminates the N+1 query problem.
     */
    private AccountDto toDtoWithChildren(AccountEntity entity) {
        var dto = GlobalMapper.INSTANCE.toDto(entity);

        dto.setAccountContacts(entity.getAccountContacts().stream()
                .map(this::toDtoWithChildren)
                .toList());

        return dto;
    }

    private AccountContactDto toDtoWithChildren(AccountContactEntity entity){
        var dto = GlobalMapper.INSTANCE.toDto(entity);
        dto.setContact(contactService.toDtoWithChildren(entity.getContact()));
        return dto;
    }
}
