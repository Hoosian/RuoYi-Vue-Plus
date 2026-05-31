package org.dromara.liferecord.domain.bo;

import org.dromara.liferecord.domain.LifeRecordChatMessage;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 聊天记录业务对象 life_record_chat_message
 *
 * @author Lion Li
 * @date 2026-05-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = LifeRecordChatMessage.class, reverseConvertGenerate = false)
public class LifeRecordChatMessageBo extends BaseEntity {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者userId（后端自动填充）
     */
    private Long fromUserId;

    /**
     * 接收者userId
     */
    @NotNull(message = "接收者不能为空", groups = { AddGroup.class })
    private Long toUserId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空", groups = { AddGroup.class })
    private String content;

    /**
     * 是否已读（0未读 1已读）
     */
    private Integer isRead;

}
