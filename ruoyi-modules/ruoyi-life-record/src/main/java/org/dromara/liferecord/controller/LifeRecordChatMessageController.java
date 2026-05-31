package org.dromara.liferecord.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.liferecord.domain.bo.LifeRecordChatMessageBo;
import org.dromara.liferecord.domain.vo.LifeRecordChatMessageVo;
import org.dromara.liferecord.service.ILifeRecordChatMessageService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;

/**
 * 聊天记录
 *
 * @author Lion Li
 * @date 2026-05-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/life-record/chat-message")
public class LifeRecordChatMessageController extends BaseController {

    private final ILifeRecordChatMessageService chatMessageService;

    /**
     * 查询聊天记录列表
     */
    @SaCheckPermission("liferecord:chatmessage:list")
    @GetMapping("/list")
    public TableDataInfo<LifeRecordChatMessageVo> list(LifeRecordChatMessageBo bo, PageQuery pageQuery) {
        return chatMessageService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取聊天记录详细信息
     *
     * @param messageId 主键
     */
    @SaCheckPermission("liferecord:chatmessage:query")
    @GetMapping("/{messageId}")
    public R<LifeRecordChatMessageVo> getInfo(@NotNull(message = "主键不能为空")
                                                @PathVariable Long messageId) {
        return R.ok(chatMessageService.queryById(messageId));
    }

    /**
     * 发送消息
     */
    @SaCheckPermission("liferecord:chatmessage:add")
    @Log(title = "聊天记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/send")
    public R<LifeRecordChatMessageVo> send(@Validated(AddGroup.class) @RequestBody LifeRecordChatMessageBo bo) {
        bo.setFromUserId(LoginHelper.getUserId());
        LifeRecordChatMessageVo vo = chatMessageService.sendMessage(bo);
        return R.ok(vo);
    }

    /**
     * 查询两人聊天记录
     *
     * @param userId 对方用户ID
     */
    @SaCheckPermission("liferecord:chatmessage:list")
    @GetMapping("/history/{userId}")
    public TableDataInfo<LifeRecordChatMessageVo> history(@NotNull(message = "用户ID不能为空") @PathVariable Long userId,
                                                            PageQuery pageQuery) {
        Long currentUserId = LoginHelper.getUserId();
        return chatMessageService.queryChatHistory(currentUserId, userId, pageQuery);
    }

    /**
     * 查询当前用户的未读消息列表
     */
    @SaCheckPermission("liferecord:chatmessage:list")
    @GetMapping("/unread")
    public R<List<LifeRecordChatMessageVo>> unread() {
        Long currentUserId = LoginHelper.getUserId();
        return R.ok(chatMessageService.queryUnreadList(currentUserId));
    }

    /**
     * 标记某人的消息为已读
     *
     * @param fromUserId 发送者用户ID
     */
    @SaCheckPermission("liferecord:chatmessage:edit")
    @Log(title = "聊天记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/read/{fromUserId}")
    public R<Integer> read(@NotNull(message = "发送者ID不能为空") @PathVariable Long fromUserId) {
        Long currentUserId = LoginHelper.getUserId();
        int count = chatMessageService.markAsRead(currentUserId, fromUserId);
        return R.ok(count);
    }

    /**
     * 修改聊天记录
     */
    @SaCheckPermission("liferecord:chatmessage:edit")
    @Log(title = "聊天记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated @RequestBody LifeRecordChatMessageBo bo) {
        return toAjax(chatMessageService.updateByBo(bo));
    }

    /**
     * 删除聊天记录
     *
     * @param messageIds 主键串
     */
    @SaCheckPermission("liferecord:chatmessage:remove")
    @Log(title = "聊天记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{messageIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] messageIds) {
        return toAjax(chatMessageService.deleteWithValidByIds(List.of(messageIds), true));
    }
}
