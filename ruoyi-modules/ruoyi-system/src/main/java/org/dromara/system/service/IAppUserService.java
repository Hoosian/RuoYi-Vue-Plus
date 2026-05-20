package org.dromara.system.service;

import org.dromara.system.domain.AppUser;
import org.dromara.system.domain.vo.AppUserVo;

/**
 * C端用户 业务层
 */
public interface IAppUserService {

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    AppUserVo selectByOpenid(String openid);

    /**
     * 根据用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    AppUserVo selectUserById(Long userId);

    /**
     * 新增C端用户
     *
     * @param user 用户信息
     * @return 结果
     */
    int insertUser(AppUser user);

}
