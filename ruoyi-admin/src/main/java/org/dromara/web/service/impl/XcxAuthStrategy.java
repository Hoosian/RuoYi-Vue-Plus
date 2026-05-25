package org.dromara.web.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.domain.model.XcxLoginBody;
import org.dromara.common.core.domain.model.XcxLoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.AppUser;
import org.dromara.system.domain.vo.AppUserVo;
import org.dromara.system.service.IAppUserService;
import org.dromara.web.config.WxMiniappProperties;
import org.dromara.web.domain.vo.LoginVo;
import org.dromara.web.service.IAuthStrategy;
import org.dromara.system.domain.vo.SysClientVo;
import org.springframework.stereotype.Service;

/**
 * 小程序认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("xcx" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class XcxAuthStrategy implements IAuthStrategy {

    private final IAppUserService appUserService;
    private final WxMiniappProperties wxMiniappProperties;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        ValidatorUtils.validate(loginBody);
        // xcxCode 为 小程序调用 wx.login 授权后获取
        String xcxCode = loginBody.getXcxCode();
        // 多个小程序识别使用
        String appid = loginBody.getAppid();

        // 1. 根据请求中的 appid 从配置中读取对应的 appsecret
        String appSecret = wxMiniappProperties.getConfigs().get(appid);
        if (StringUtils.isBlank(appSecret)) {
            throw new ServiceException("未配置小程序 appsecret: " + appid);
        }

        // 2. 调用微信登录凭证校验接口，获取 session_key 与 openid
        AuthRequest authRequest = new AuthWechatMiniProgramRequest(AuthConfig.builder()
            .clientId(appid).clientSecret(appSecret)
            .ignoreCheckRedirectUri(true).ignoreCheckState(true).build());
        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(xcxCode);
        AuthResponse<AuthUser> resp = authRequest.login(authCallback);
        String openid;
        if (resp.ok()) {
            AuthToken token = resp.getData().getToken();
            openid = token.getOpenId();
            // 微信小程序只有关联到微信开放平台下之后才能获取到 unionId，因此unionId不一定能返回。
            // unionId = token.getUnionId();
        } else {
            throw new ServiceException(resp.getMsg());
        }
        // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
        AppUserVo user = loadUserByOpenid(openid);
        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getNickName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(UserType.APP_USER.getUserType());
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        loginUser.setOpenid(openid);

        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        loginVo.setOpenid(openid);
        return loginVo;
    }

    private AppUserVo loadUserByOpenid(String openid) {
        // 使用 openid 查询 C 端用户
        AppUserVo user = appUserService.selectByOpenid(openid);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户 openid: {} 不存在，自动注册.", openid);
            // 自动注册新用户
            AppUser newUser = new AppUser();
            newUser.setNickName("微信用户" + RandomUtil.randomNumbers(4));
            newUser.setOpenid(openid);
            newUser.setStatus(SystemConstants.NORMAL);
            appUserService.insertUser(newUser);
            // 重新查询获取完整用户信息（包括自增的 user_id）
            user = appUserService.selectByOpenid(openid);
        }
        if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", openid);
            throw new ServiceException("用户已被停用");
        }
        return user;
    }

}
