package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.AccountDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.lvo.platform.AccountStatusLvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class AccountMapper extends AbstractMapper {

    private final AccountDao accountDao;

    private final ContactDao contactDao;

    @Autowired
    public AccountMapper(AccountDao dao, ContactDao contactDao) {
        this.accountDao = dao;
        this.contactDao = contactDao;
    }

    public AccountEntity mappingForCreate(AccountDto dto) {
        var entity = new AccountEntity();
        entity.setName(dto.getName());
        entity.setStatusDate(LocalDateTime.now());
        entity.setStatus(AccountStatusLvo.ACTIVE);
        entity.setDescription(dto.getDescription());
        return beforeCreateHistEntity(entity);
    }

    public AccountEntity mappingForUpdate(AccountDto dto) {
        var entity = accountDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + dto.getId()));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return beforeUpdateHistEntity(entity);
    }

    public AccountEntity mappingForStatusChange(AccountDto dto) {
        var entity = accountDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + dto.getId()));
        entity.setStatus(dto.getStatus());
        entity.setStatusDate(dto.getStatusDate());
        entity.setStatusReason(dto.getStatusReason());
        return entity;
    }

    public AccountContactEntity mappingForCreate(AccountEntity entity, AccountContactDto dto) {
        var contact = dto.getContact();
        Objects.requireNonNull(contact, "Contact must not be null in AccountContactDto for delete operation");
        Objects.requireNonNull(contact.getId(), "Contact ID must not be null in AccountContactDto for delete operation");

        var child = new AccountContactEntity();
        var contactEntity = contactDao.findById(contact.getId()).orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + contact.getId()));
        child.setAccount(entity);
        child.setRole(dto.getRole());
        child.setContact(contactEntity);
        return beforeCreateHistEntity(child);
    }
}
