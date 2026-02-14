package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dao.platform.UserDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.common.DefaultSearchCriteria;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUser;
import ca.bigmwaj.emapp.as.dto.security.AuthenticatedUserGrantedAuthority;
import ca.bigmwaj.emapp.as.dto.shared.SearchResultDto;
import ca.bigmwaj.emapp.as.dto.platform.UserDto;
import ca.bigmwaj.emapp.as.dto.shared.search.SearchInfos;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.AccountEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.UserEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import ca.bigmwaj.emapp.as.service.ServiceException;
import ca.bigmwaj.emapp.dm.lvo.platform.HolderTypeLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UserStatusLvo;
import ca.bigmwaj.emapp.dm.lvo.platform.UsernameTypeLvo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class UserService extends AbstractService implements UserDetailsService {

    @Autowired
    private UserDao dao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ContactService contactService;

    @PersistenceContext
    private EntityManager entityManager;

    protected SearchResultDto<UserDto> searchAll() {
        var r = dao.findAll().stream().map(this::toDtoWithChildren).toList();
        return new SearchResultDto<>(r);
    }

    public SearchResultDto<UserDto> search(DefaultSearchCriteria sc) {
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

    public UserDto findById(Long userId) {
        return dao.findById(userId)
                .map(this::toDtoWithChildren)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    public void deleteById(Long userId) {
        dao.deleteById(userId);
    }

    private ContactEntity getContact(UserDto dto){
        var entity = GlobalMapper.INSTANCE.toEntity(dto.getContact());
        if( entity.getId() == null ){

            beforeCreateHistEntity(entity);
            entity = contactDao.save(entity);
        }else{
            entity = contactDao.getReferenceById(entity.getId());
        }

        return entity;
    }

    public UserDto create(UserDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        entity.setContact(getContact(dto));
        beforeCreateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
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
        entity.setHolderType(HolderTypeLvo.ACCOUNT);
        entity.setStatus(UserStatusLvo.ACTIVE);
        entity.setStatusDate(LocalDateTime.now());
        beforeCreateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    public UserDto update(UserDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        beforeUpdateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var msgTpl = "Aucun utilisateur trouvé avant pour nom d'utilisateur %s";
        Supplier<UsernameNotFoundException> ex;
        ex = () -> new UsernameNotFoundException(String.format(msgTpl, username));
        var user =  dao.findByUsernameIgnoreCase(username).orElseThrow(ex);

        var status = user.getStatus();
//		var passwordLastChangeDate = user.getPasswordLastChangeDate().getValue();

//		LocalDateTime.now().minus(passwordLastChangeDate.);

        boolean enabled = UserStatusLvo.ACTIVE.equals(status);
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        var authorities = Collections.singleton(new AuthenticatedUserGrantedAuthority("USER"));
        return new AuthenticatedUser(GlobalMapper.INSTANCE.toDto(user), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
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
        var dto = GlobalMapper.INSTANCE.toDto(entity);
        dto.setContact(contactService.toDtoWithChildren(entity.getContact()));
        return dto;
    }
}
