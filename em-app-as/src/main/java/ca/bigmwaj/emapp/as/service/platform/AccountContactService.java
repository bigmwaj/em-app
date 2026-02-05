package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.AccountContactDao;
import ca.bigmwaj.emapp.as.dao.platform.ContactDao;
import ca.bigmwaj.emapp.as.dto.GlobalMapper;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Service
public class AccountContactService extends AbstractService {

    @Autowired
    private AccountContactDao dao;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactDao contactDao;

    public AccountContactDto create(AccountContactDto dto) {
        var entity = GlobalMapper.INSTANCE.toEntity(dto);
        var contact = contactService.create(dto.getContact());
        var contactEntity = contactDao.getReferenceById(contact.getId());
        entity.setContact(contactEntity);
        beforeCreateHistEntity(entity);
        return GlobalMapper.INSTANCE.toDto(dao.save(entity));
    }
}
