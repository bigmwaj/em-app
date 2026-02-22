package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class AccountService extends AbstractMainService<AccountDto, AccountEntity, Short> {

    @Autowired
    private AccountDao dao;

    @Autowired
    private AccountContactService accountContactService;

    @Autowired
    private UserService userService;

    @Override
    protected Function<AccountEntity, AccountDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected AccountDao getDao() {
        return dao;
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
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeCreate(entity, dto);
        entity = dao.save(entity);
        userService.create(entity, dto.getAdminUsername(), dto.getAdminUsernameType());
        return GlobalPlatformMapper.INSTANCE.toDto(entity);
    }

    public void beforeCreate(AccountEntity entity, AccountDto dto) {
        beforeCreateHistEntity(entity);

        List<AccountContactEntity> accountContacts = entity.getAccountContacts();
        if( accountContacts == null || accountContacts.isEmpty()){
            return;
        }

        for(AccountContactEntity accountContact : accountContacts) {
            accountContact.setAccount(entity);
            accountContactService.beforeCreate(accountContact, null);
        }
    }

    public AccountDto update(AccountDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        entity = dao.saveAndFlush(entity);
        return findById(entity.getId());
    }

    public AccountDto changeStatus(AccountDto dto) {
        AccountEntity entity = dao.findById(dto.getId()).orElseThrow(() -> new NoSuchElementException("Account not found with id: " + dto.getId()));
        entity.setStatus(dto.getStatus());
        entity.setStatusDate(dto.getStatusDate());
        entity.setStatusReason(dto.getStatusReason());
        beforeUpdateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    private AccountDto toDtoWithChildren(AccountEntity entity) {
        var dto = GlobalPlatformMapper.INSTANCE.toDto(entity);

        dto.setAccountContacts(entity.getAccountContacts().stream()
                .map(accountContactService::toDtoWithChildren)
                .toList());

        return dto;
    }
}
