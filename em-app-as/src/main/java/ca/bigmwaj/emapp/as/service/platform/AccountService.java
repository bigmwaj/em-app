package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountFilterDto;
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
    private AccountContactDao accountContactDao;

    @Autowired
    private AccountContactService accountContactService;

    @PersistenceContext
    private EntityManager entityManager;

    protected SearchResultDto<AccountDto> searchAll() {
        var r = dao.findAll().stream().map(GlobalMapper.INSTANCE::toDto).map(this::addChildren).toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<AccountDto> search(AccountFilterDto sc) {
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
                .map(GlobalMapper.INSTANCE::toDto)
                .map(this::addChildren)
                .toList();

        return new SearchResultDto<>(searchStats, r);
    }

    public AccountDto findById(Long accountId) {
        return dao.findById(accountId)
                .map(GlobalMapper.INSTANCE::toDto)
                .map(this::addChildren)
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
        if (dto.getContacts() != null) {
            for (var acDto : dto.getContacts()) {
                acDto.setAccountId(entity.getId());
                accountContactService.create(acDto);
            }
        }
    }

    private AccountDto addChildren(AccountDto dto){
        addAccountContacts(dto);
        return dto;
    }

    private void addAccountContacts(AccountDto dto){
        var l = accountContactDao.findAllByAccountId(dto.getId())
                .stream().map(GlobalMapper.INSTANCE::toDto).toList();
        dto.setContacts(l);
    }
}
