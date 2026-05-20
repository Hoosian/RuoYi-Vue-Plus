package org.dromara.system.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.domain.AppUser;
import org.dromara.system.domain.vo.AppUserVo;
import org.dromara.system.mapper.AppUserMapper;
import org.dromara.system.service.IAppUserService;
import org.springframework.stereotype.Service;

/**
 * C端用户 业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements IAppUserService {

    private final AppUserMapper baseMapper;

    @Override
    public AppUserVo selectByOpenid(String openid) {
        return baseMapper.selectByOpenid(openid);
    }

    @Override
    public AppUserVo selectUserById(Long userId) {
        return baseMapper.selectVoById(userId);
    }

    @Override
    public int insertUser(AppUser user) {
        return baseMapper.insert(user);
    }

}
