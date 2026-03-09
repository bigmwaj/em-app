package ca.bigmwaj.emapp.as.mapper;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.lvo.platform.AccountContactRoleLvo;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class UserMapper extends AbstractMapper {

    //    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ContactDao contactDao;

    private final UserDao userDao;

    private final AccountContactDao accountContactDao;

    public UserMapper(ContactDao contactDao, UserDao userDao, AccountContactDao accountContactDao) {
        this.contactDao = contactDao;
        this.userDao = userDao;
        this.accountContactDao = accountContactDao;
    }

    public UserEntity mappingForCreate(UserDto dto) {
        var contact = dto.getContact();
        Objects.requireNonNull(contact, "Contact must not be null");
        Objects.requireNonNull(contact.getId(), "Contact ID must not be null");

        var contactEntity = contactDao.findById(contact.getId()).orElseThrow(() -> new IllegalArgumentException("Contact not found with id: " + contact.getId()));
        var entity = new UserEntity();
        entity.setOwnerType(contact.getOwnerType());
        entity.setUsername(dto.getUsername());
        entity.setContact(contactEntity);
        entity.setStatus(UserStatusLvo.ACTIVE);
        entity.setStatusDate(LocalDateTime.now());
        entity.setUsernameType(dto.getUsernameType());
        entity.setProvider(dto.getProvider());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return beforeCreateHistEntity(entity);
    }

    private ContactEntity getPrincipalContactForAccount(Short accountId) {
        return accountContactDao.findByAccountIdAndRole(accountId, AccountContactRoleLvo.PRINCIPAL)
                .map(AccountContactEntity::getContact)
                .orElseThrow(() -> new IllegalArgumentException("Principal contact not found for account id: " + accountId));
    }

    public UserEntity mappingForCreate(AccountDto dto) {
        Objects.requireNonNull(dto.getId(), "Account ID must not be null");
        var contact = getPrincipalContactForAccount(dto.getId());
        var entity = new UserEntity();
        entity.setOwnerType(contact.getOwnerType());
        entity.setUsername(dto.getAdminUsername());
        entity.setContact(contact);
        entity.setStatus(UserStatusLvo.ACTIVE);
        entity.setStatusDate(LocalDateTime.now());
        entity.setUsernameType(dto.getAdminUsernameType());
        entity.setPassword("To-be-updated");

        return beforeCreateHistEntity(entity);
    }

    public UserEntity mappingForUpdate(UserDto dto) {
        var entity = userDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getId()));
        entity.setUsernameType(dto.getUsernameType());
        entity.setProvider(dto.getProvider());
        return entity;
    }

    public UserEntity mappingForPasswordChange(UserDto dto) {
        var entity = userDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getId()));
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public UserEntity mappingForStatusChange(UserDto dto) {
        var entity = userDao.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getId()));
        entity.setStatus(dto.getStatus());
        entity.setStatusDate(dto.getStatusDate());
        entity.setStatusReason(dto.getStatusReason());
        return entity;
    }
}
