package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dto.platform.UserRoleDto;
import ca.bigmwaj.emapp.as.entity.platform.UserRoleEntity;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class UserRoleService extends AbstractBaseService<UserRoleDto, UserRoleEntity> {

}
