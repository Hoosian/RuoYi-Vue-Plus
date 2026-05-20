package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AppUser;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * C端用户信息视图对象 app_user
 */
@Data
@AutoMapper(target = AppUser.class)
public class AppUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;

}
