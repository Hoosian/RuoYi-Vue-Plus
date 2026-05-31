package org.dromara.liferecord.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.sse.utils.SseMessageUtils;
import org.dromara.liferecord.domain.LifeRecordChatMessage;
import org.dromara.liferecord.domain.bo.LifeRecordChatMessageBo;
import org.dromara.liferecord.domain.vo.LifeRecordChatMessageVo;
import org.dromara.liferecord.mapper.LifeRecordChatMessageMapper;
import org.dromara.liferecord.service.ILifeRecordChatMessageService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 聊天记录Service业务层处理
 *
 * @author Lion Li
 * @date 2026-05-31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LifeRecordChatMessageServiceImpl implements ILifeRecordChatMessageService {

    private final LifeRecordChatMessageMapper baseMapper;

    /**
     * 查询聊天记录
     *
     * @param messageId 主键
     * @return 聊天记录
     */
    @Override
    public LifeRecordChatMessageVo queryById(Long messageId) {
        return baseMapper.selectVoById(messageId);
    }

    /**
     * 分页查询聊天记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 聊天记录分页列表
     */
    @Override
    public TableDataInfo<LifeRecordChatMessageVo> queryPageList(LifeRecordChatMessageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<LifeRecordChatMessage> lqw = buildQueryWrapper(bo);
        Page<LifeRecordChatMessageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的聊天记录列表
     *
     * @param bo 查询条件
     * @return 聊天记录列表
     */
    @Override
    public List<LifeRecordChatMessageVo> queryList(LifeRecordChatMessageBo bo) {
        LambdaQueryWrapper<LifeRecordChatMessage> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<LifeRecordChatMessage> buildQueryWrapper(LifeRecordChatMessageBo bo) {
        LambdaQueryWrapper<LifeRecordChatMessage> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getFromUserId() != null, LifeRecordChatMessage::getFromUserId, bo.getFromUserId());
        lqw.eq(bo.getToUserId() != null, LifeRecordChatMessage::getToUserId, bo.getToUserId());
        lqw.eq(bo.getIsRead() != null, LifeRecordChatMessage::getIsRead, bo.getIsRead());
        lqw.orderByDesc(LifeRecordChatMessage::getCreateTime);
        return lqw;
    }

    /**
     * 发送消息
     *
     * @param bo 消息
     * @return 发送的消息VO
     */
    @Override
    public LifeRecordChatMessageVo sendMessage(LifeRecordChatMessageBo bo) {
        LifeRecordChatMessage add = MapstructUtils.convert(bo, LifeRecordChatMessage.class);
        add.setIsRead(0);
        baseMapper.insert(add);

        LifeRecordChatMessageVo vo = MapstructUtils.convert(add, LifeRecordChatMessageVo.class);

        // 通过SSE推送给接收者
        try {
            SseMessageUtils.sendMessage(bo.getToUserId(), cn.hutool.json.JSONUtil.toJsonStr(vo));
        } catch (Exception e) {
            log.warn("SSE推送消息失败, toUserId={}, messageId={}", bo.getToUserId(), add.getMessageId(), e);
        }

        return vo;
    }

    /**
     * 查询两人聊天记录
     *
     * @param userId1   用户1
     * @param userId2   用户2
     * @param pageQuery 分页参数
     * @return 聊天记录分页列表
     */
    @Override
    public TableDataInfo<LifeRecordChatMessageVo> queryChatHistory(Long userId1, Long userId2, PageQuery pageQuery) {
        List<LifeRecordChatMessageVo> list = baseMapper.selectChatHistory(userId1, userId2);
        // 内存分页（因为SQL用了or条件，MyBatisPlus分页插件不兼容）
        return TableDataInfo.build(list, pageQuery.build());
    }

    /**
     * 查询未读消息列表
     *
     * @param toUserId 接收者
     * @return 未读消息列表
     */
    @Override
    public List<LifeRecordChatMessageVo> queryUnreadList(Long toUserId) {
        LambdaQueryWrapper<LifeRecordChatMessage> lqw = Wrappers.lambdaQuery();
        lqw.eq(LifeRecordChatMessage::getToUserId, toUserId);
        lqw.eq(LifeRecordChatMessage::getIsRead, 0);
        lqw.eq(LifeRecordChatMessage::getDelFlag, "0");
        lqw.orderByAsc(LifeRecordChatMessage::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 标记消息为已读
     *
     * @param toUserId   接收者
     * @param fromUserId 发送者
     * @return 标记已读的数量
     */
    @Override
    public int markAsRead(Long toUserId, Long fromUserId) {
        LambdaUpdateWrapper<LifeRecordChatMessage> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(LifeRecordChatMessage::getToUserId, toUserId);
        updateWrapper.eq(LifeRecordChatMessage::getFromUserId, fromUserId);
        updateWrapper.eq(LifeRecordChatMessage::getIsRead, 0);
        updateWrapper.set(LifeRecordChatMessage::getIsRead, 1);
        return baseMapper.update(updateWrapper);
    }

    /**
     * 修改聊天记录
     *
     * @param bo 聊天记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(LifeRecordChatMessageBo bo) {
        LifeRecordChatMessage update = MapstructUtils.convert(bo, LifeRecordChatMessage.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验并批量删除聊天记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
