package org.dromara.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序配置
 *
 * 支持多小程序配置，key 为 appid，value 为 appsecret
 * 示例：
 *   wx:
 *     miniapp:
 *       configs:
 *         wx1234567890abcdef: abcdef1234567890abcdef1234567890
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniappProperties {

    /**
     * 小程序配置，key=appid, value=appsecret
     */
    private Map<String, String> configs = new HashMap<>();

}
