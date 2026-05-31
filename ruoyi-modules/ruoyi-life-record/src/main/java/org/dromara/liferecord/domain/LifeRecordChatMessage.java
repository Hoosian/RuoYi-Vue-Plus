package org.dromara.liferecord.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 聊天记录对象 life_record_chat_message
 *
 * @author Lion Li
 * @date 2026-05-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("life_record_chat_message")
public class LifeRecordChatMessage extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "message_id")
    private Long messageId;

    /**
     * 发送者userId
     */
    private Long fromUserId;

    /**
     * 接收者userId
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读（0未读 1已读）
     */
    private Integer isRead;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

}
