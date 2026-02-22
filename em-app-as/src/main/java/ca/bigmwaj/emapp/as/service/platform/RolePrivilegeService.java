package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dto.platform.RolePrivilegeDto;
import ca.bigmwaj.emapp.as.entity.platform.RolePrivilegeEntity;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class RolePrivilegeService extends AbstractBaseService<RolePrivilegeDto, RolePrivilegeEntity> {

}
