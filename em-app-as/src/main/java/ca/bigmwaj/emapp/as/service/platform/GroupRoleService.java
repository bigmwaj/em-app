package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dto.platform.GroupRoleDto;
import ca.bigmwaj.emapp.as.entity.platform.GroupRoleEntity;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class GroupRoleService extends AbstractBaseService<GroupRoleDto, GroupRoleEntity> {

}
