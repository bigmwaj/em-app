package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class AccountContactService extends AbstractService {

    @Autowired
    private AccountContactDao dao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactDao contactDao;

    public AccountContactDto create(AccountContactDto dto) {
        var entity = GlobalPlatformMapper.INSTANCE.toEntity(dto);
        var contact = contactService.create(dto.getContact());
        var contactEntity = contactDao.getReferenceById(contact.getId());
        entity.setContact(contactEntity);
        beforeCreateHistEntity(entity);
        return GlobalPlatformMapper.INSTANCE.toDto(dao.save(entity));
    }

    public void beforeCreate(AccountContactEntity entity, AccountContactDto dto) {
        beforeCreateHistEntity(entity);
        var contactToCreate = entity.getContact();
        if (contactToCreate == null) {
            return;
        }
        contactService.beforeCreate(contactToCreate, null);
    }

    protected AccountContactDto toDtoWithChildren(AccountContactEntity entity) {
        var dto = GlobalPlatformMapper.INSTANCE.toDto(entity);
        ContactEntity contactEntity = entity.getContact();
        ContactDto contactDto = contactService.toDtoWithChildren(contactEntity);
        dto.setContact(contactDto);
        return dto;
    }
}
