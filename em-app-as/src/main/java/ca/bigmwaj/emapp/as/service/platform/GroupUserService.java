package ca.bigmwaj.emapp.as.service.platform;

import ca.bigmwaj.emapp.as.dao.platform.GroupUserDao;
import ca.bigmwaj.emapp.as.dto.platform.GroupUserDto;
import ca.bigmwaj.emapp.as.entity.platform.GroupUserEntity;
import ca.bigmwaj.emapp.as.service.AbstractBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class GroupUserService extends AbstractBaseService<GroupUserDto, GroupUserEntity> {

}
