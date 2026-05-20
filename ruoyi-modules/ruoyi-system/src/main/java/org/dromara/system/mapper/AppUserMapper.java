package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.AppUser;
import org.dromara.system.domain.vo.AppUserVo;

/**
 * C端用户表 数据层
 */
public interface AppUserMapper extends BaseMapperPlus<AppUser, AppUserVo> {

    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    default AppUserVo selectByOpenid(String openid) {
        return this.selectVoOne(new LambdaQueryWrapper<AppUser>()
            .eq(AppUser::getOpenid, openid));
    }

}
