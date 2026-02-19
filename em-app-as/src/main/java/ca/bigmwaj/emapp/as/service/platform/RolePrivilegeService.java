package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.RolePrivilegeDao;
import ca.bigmwaj.emapp.as.dto.platform.AccountContactDto;
import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.entity.platform.AccountContactEntity;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class RolePrivilegeService extends AbstractService {

    @Autowired
    private RolePrivilegeDao dao;

    public void beforeCreate(RolePrivilegeEntity entity, RolePrivilegeDto dto) {
        beforeCreateHistEntity(entity);
        var roleToCreate = entity.getRole();
        if (roleToCreate == null) {
            return;
        }
        //contactService.beforeCreate(contactToCreate, null);
    }

}
