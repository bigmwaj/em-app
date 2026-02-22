package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalPlatformMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.ContactDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.ContactEntity;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class AccountContactService extends AbstractBaseService<AccountContactDto, AccountContactEntity> {

    @Autowired
    private AccountContactDao dao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactDao contactDao;

    public void beforeCreate(AccountContactEntity entity, AccountContactDto dto) {
        beforeCreateHistEntity(entity);
        ContactEntity contactToCreate = entity.getContact();
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
