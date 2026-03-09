package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import static ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper.INSTANCE;
import ca.bigmwaj.emapp.as.dto.platform.AccountDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUserGrantedAuthority;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.integration.KafkaPublisher;
import ca.bigmwaj.emapp.as.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.as.mapper.UserMapper;
import ca.bigmwaj.emapp.as.service.AbstractMainService;
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

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class UserService extends AbstractMainService<UserDto, UserEntity, Short> implements AuthenticationManager {

    private final UserDao dao;

    private final UserMapper mapper;

    private final ContactService contactService;

    private final KafkaPublisher kafkaPublisher;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserDao dao, UserMapper mapper, ContactService contactService, KafkaPublisher kafkaPublisher) {
        this.dao = dao;
        this.mapper = mapper;
        this.contactService = contactService;
        this.kafkaPublisher = kafkaPublisher;
    }

    public UserDto create(UserDto dto) {
        var contact = dto.getContact();
        contact = contactService.create(contact);

        dto.setContact(contact);

        var entity = mapper.mappingForCreate(dto);

        dto = INSTANCE.toDto(dao.save(entity));

        kafkaPublisher.publish("user-created", dto);
        return dto;
    }

    public void createAccountAdminUser(AccountDto accountDto) {
        var dto = INSTANCE.toDto(dao.save(mapper.mappingForCreate(accountDto)));
        kafkaPublisher.publish("user-created", dto);
    }

    public UserDto update(UserDto dto) {
        return INSTANCE.toDto(dao.save(mapper.mappingForUpdate(dto)));
    }

    public UserDto changePassword(UserDto dto) {
        return INSTANCE.toDto(dao.save(mapper.mappingForPasswordChange(dto)));
    }

    public UserDto changeStatus(UserDto dto) {
        return INSTANCE.toDto(dao.save(mapper.mappingForStatusChange(dto)));
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
            var authUser = new AuthenticatedUser(INSTANCE.toDto(user), enabled,
                    accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            return UsernamePasswordAuthenticationToken.authenticated(authUser, username, authorities);

        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    protected Function<UserEntity, UserDto> getEntityToDtoMapper() {
        return INSTANCE::toDto;
    }

    @Override
    protected UserDao getDao() {
        return dao;
    }
}
