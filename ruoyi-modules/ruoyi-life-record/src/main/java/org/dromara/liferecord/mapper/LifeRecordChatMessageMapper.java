package org.dromara.liferecord.mapper;

import org.dromara.liferecord.domain.LifeRecordChatMessage;
import org.dromara.liferecord.domain.vo.LifeRecordChatMessageVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 聊天记录Mapper接口
 *
 * @author Lion Li
 * @date 2026-05-31
 */
public interface LifeRecordChatMessageMapper extends BaseMapperPlus<LifeRecordChatMessage, LifeRecordChatMessageVo> {

    /**
     * 查询两人的聊天记录
     *
     * @param userId1 用户1
     * @param userId2 用户2
     * @return 聊天记录列表
     */
    List<LifeRecordChatMessageVo> selectChatHistory(Long userId1, Long userId2);

}
