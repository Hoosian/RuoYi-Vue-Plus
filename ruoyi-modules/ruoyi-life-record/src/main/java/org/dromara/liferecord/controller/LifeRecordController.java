package org.dromara.liferecord.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
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
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.liferecord.domain.vo.LifeRecordVo;
import org.dromara.liferecord.domain.bo.LifeRecordBo;
import org.dromara.liferecord.service.ILifeRecordService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import java.util.Date;

/**
 * 日记
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/life-record/life-record")
public class LifeRecordController extends BaseController {

    private final ILifeRecordService lifeRecordService;

    /**
     * 查询日记列表
     */
    @SaCheckPermission("life-record:life-record:list")
    @GetMapping("/list")
    public TableDataInfo<LifeRecordVo> list(LifeRecordBo bo, PageQuery pageQuery) {
        return lifeRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出日记列表
     */
    @SaCheckPermission("life-record:life-record:export")
    @Log(title = "日记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(LifeRecordBo bo, HttpServletResponse response) {
        List<LifeRecordVo> list = lifeRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "日记", LifeRecordVo.class, response);
    }

    /**
     * 获取日记详细信息
     *
     * @param recordId 主键
     */
    @SaCheckPermission("life-record:life-record:query")
    @GetMapping("/{recordId}")
    public R<LifeRecordVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long recordId) {
        return R.ok(lifeRecordService.queryById(recordId));
    }

    /**
     * 新增日记
     */
    @SaCheckPermission("life-record:life-record:add")
    @Log(title = "日记", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody LifeRecordBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        if (bo.getRecordDate() == null) {
            bo.setRecordDate(new Date());
        }
        return toAjax(lifeRecordService.insertByBo(bo));
    }

    /**
     * 修改日记
     */
    @SaCheckPermission("life-record:life-record:edit")
    @Log(title = "日记", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody LifeRecordBo bo) {
        return toAjax(lifeRecordService.updateByBo(bo));
    }

    /**
     * 修改日记状态（下架/恢复）
     */
    @SaCheckPermission("life-record:life-record:edit")
    @Log(title = "日记", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/status")
    public R<Void> changeStatus(@NotNull(message = "主键不能为空") @RequestParam Long recordId,
                                  @NotNull(message = "状态不能为空") @RequestParam Long status) {
        return toAjax(lifeRecordService.updateStatus(recordId, status));
    }

    /**
     * 删除日记
     *
     * @param recordIds 主键串
     */
    @SaCheckPermission("life-record:life-record:remove")
    @Log(title = "日记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] recordIds) {
        return toAjax(lifeRecordService.deleteWithValidByIds(List.of(recordIds), true));
    }
}
