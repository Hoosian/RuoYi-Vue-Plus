package org.dromara.liferecord.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.dromara.liferecord.domain.LifeRecordChatMessage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 聊天记录视图对象 life_record_chat_message
 *
 * @author Lion Li
 * @date 2026-05-31
 */
@Data
@AutoMapper(target = LifeRecordChatMessage.class)
public class LifeRecordChatMessageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

    /**
     * 发送者userId
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromUserId;

    /**
     * 接收者userId
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
