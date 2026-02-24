package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUserGrantedAuthority;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import ca.bigmwaj.emapp.dm.lvo.platform.OwnerTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class UserService extends AbstractMainService<UserDto, UserEntity, Short> implements AuthenticationManager {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao dao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ContactService contactService;

    //    @Autowired
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDto findById(Short userId) {
        return dao.findById(userId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    public void deleteById(Short userId) {
        dao.deleteById(userId);
    }

    public UserDto create(UserDto dto) {
        UserEntity entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        // Hash password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        beforeCreateHistEntity(entity);
        ContactEntity contactEntity = contactService.createAndReturnEntity(dto.getContact());
        entity.setContact(contactEntity);

        entity = dao.save(entity);
        dto = GlobalPlatformMapper.INSTANCE.toDto(entity);
        dto.setContact(contactService.toDtoWithChildren(entity.getContact()));

        return dto;
    }

    public UserDto create(AccountEntity accountEntity, String username, UsernameTypeLvo usernameType) {
        Objects.requireNonNull(accountEntity, "Account entity must not be null");
        Objects.requireNonNull(accountEntity.getAccountContacts(), "Account entity must have account contacts");

        UserEntity entity = new UserEntity();
        ContactEntity primaryContact = accountEntity.getAccountContacts()
                .stream().findFirst()
                .map(AccountContactEntity::getContact).orElseThrow(() -> new ServiceException("Account must have at least one contact"));
        entity.setContact(primaryContact);
        entity.setUsername(username);
        entity.setPassword("to-be-updated");
        entity.setUsernameType(usernameType);
        entity.setOwnerType(OwnerTypeLvo.ACCOUNT);
        entity.setStatus(UserStatusLvo.ACTIVE);
        entity.setStatusDate(LocalDateTime.now());
        beforeCreateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    public UserDto update(UserDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);

        // Hash password if provided and changed
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            // Preserve existing password if not provided
            var existingUser = dao.findById(dto.getId());
            existingUser.ifPresent(user -> entity.setPassword(user.getPassword()));
        }

        beforeUpdateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    public void validateAccountHolder(String email) {
        var optionalUserEntity = dao.findByUsernameIgnoreCase(email);
        if (optionalUserEntity.isEmpty()) {
            throw new NoSuchElementException("No user found with email: " + email);
        }

        var userEntity = optionalUserEntity.get();
        if (!UserStatusLvo.ACTIVE.equals(userEntity.getStatus())) {
            throw new IllegalStateException("User account is not active for email: " + email);
        }
    }

    public boolean isUsernameUnique(String username) {
        return !dao.existsByUsername(username);
    }

    protected UserDto toDtoWithChildren(UserEntity entity) {
        var dto = GlobalPlatformMapper.INSTANCE.toDto(entity);
        dto.setContact(contactService.toDtoWithChildren(entity.getContact()));
        return dto;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var username = authentication.getName();
        var password = authentication.getCredentials().toString();
        var user = dao.findByUsernameIgnoreCase(username).orElse(null);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            var status = user.getStatus();
            boolean enabled = UserStatusLvo.ACTIVE.equals(status);
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;
            var authorities = Collections.singleton(new AuthenticatedUserGrantedAuthority("USER"));
            var authUser = new AuthenticatedUser(GlobalPlatformMapper.INSTANCE.toDto(user), enabled,
                    accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            return UsernamePasswordAuthenticationToken.authenticated(authUser, username, authorities);

        }else{
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    protected Function<UserEntity, UserDto> getEntityToDtoMapper() {
        return GlobalPlatformMapper.INSTANCE::toDto;
    }

    @Override
    protected UserDao getDao() {
        return dao;
    }
}
