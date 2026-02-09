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
import ca.bigmwaj.emapp.dm.lvo.platform.AccountContactRoleLvo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        var r = dao.findAll().stream().map(GlobalMapper.INSTANCE::toDto).toList();
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
                .map(GlobalMapper.INSTANCE::toDto)
                .map(e -> addChildren(sc, e))
                .toList();

        return new SearchResultDto<>(searchStats, r);
    }

    public AccountDto findById(Long accountId) {
        return dao.findById(accountId)
                .map(GlobalMapper.INSTANCE::toDto)
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

    private AccountDto addChildren(AccountSearchCriteria sc, AccountDto dto){
        if( sc.isIncludeContactRoles()){
            dto.setAccountContacts(getAccountContacts(dto));
        }

        if( sc.isIncludeMainContact()){
            dto.setMainContact(getMainAccountContact(dto).getContact());
        }
        return dto;
    }

    private List<AccountContactDto> getAccountContacts(AccountDto dto){
        return accountContactDao.findAllByAccountId(dto.getId())
                .stream().map(this::map).toList();
    }

    private AccountContactDto getMainAccountContact(AccountDto dto){
        return accountContactDao.findAtMostOneByAccountIdAndRole(dto.getId(), AccountContactRoleLvo.PRINCIPAL)
                .map(this::map).orElse(null);
    }

    private AccountContactDto map(AccountContactEntity entity) {
        var dto = GlobalMapper.INSTANCE.toDto(entity);
        var contactDto = dto.getContact();
        var contactEntity = entity.getContact();

        if( contactDto != null && contactEntity != null ){
            if( contactEntity.getEmails() != null && !contactEntity.getEmails().isEmpty() ){
                contactDto.setMainEmail(GlobalMapper.INSTANCE.toDto(contactEntity.getEmails().getFirst()));
            }else{
                System.out.println("contactEntity.getEmails() = " + contactEntity.getEmails());
            }

            if( contactEntity.getPhones() != null && !contactEntity.getPhones().isEmpty() ){
                contactDto.setMainPhone(GlobalMapper.INSTANCE.toDto(contactEntity.getPhones().getFirst()));
            }else{
                System.out.println("contactEntity.getPhones() = " + contactEntity.getPhones());
            }

            if( contactEntity.getAddresses() != null && !contactEntity.getAddresses().isEmpty() ){
                contactDto.setMainAddress(GlobalMapper.INSTANCE.toDto(contactEntity.getAddresses().getFirst()));
            }else{
                System.out.println("contactEntity.getAddresses() = " + contactEntity.getAddresses());
            }
        }else {
            System.out.println("contactEntity = " + contactEntity);
            System.out.println("contactDto = " + contactDto);
        }
        return dto;
    }
}
