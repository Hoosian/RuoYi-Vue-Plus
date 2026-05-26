package org.dromara.liferecord.controller.mini;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.liferecord.domain.LifeRecord;
import org.dromara.liferecord.domain.bo.LifeRecordBo;
import org.dromara.liferecord.domain.vo.LifeRecordVo;
import org.dromara.liferecord.mapper.LifeRecordMapper;
import org.dromara.liferecord.service.ILifeRecordService;
import org.dromara.system.domain.vo.SysOssUploadVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.ISysOssService;

/**
 * 日记-小程序端
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/mini/life-record")
public class LifeRecordMiniController extends BaseController {

    private final ILifeRecordService lifeRecordService;
    private final LifeRecordMapper lifeRecordMapper;
    private final ISysOssService ossService;

    /**
     * 写日记
     */
    @PostMapping()
    public R<Void> add(@RequestBody LifeRecordBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        if (bo.getRecordDate() == null) {
            bo.setRecordDate(new Date());
        }
        return toAjax(lifeRecordService.insertByBo(bo));
    }

    /**
     * 我的日记列表
     */
    @GetMapping("/list")
    public TableDataInfo<LifeRecordVo> list(LifeRecordBo bo, PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<LifeRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LifeRecord::getUserId, userId);
        lqw.eq(bo.getRecordDate() != null, LifeRecord::getRecordDate, bo.getRecordDate());
        lqw.eq(StringUtils.isNotBlank(bo.getMood()), LifeRecord::getMood, bo.getMood());
        lqw.eq(StringUtils.isNotBlank(bo.getWeather()), LifeRecord::getWeather, bo.getWeather());
        lqw.orderByDesc(LifeRecord::getRecordDate);
        Page<LifeRecordVo> result = lifeRecordMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 日记详情
     */
    @GetMapping("/{recordId}")
    public R<LifeRecordVo> getInfo(@PathVariable Long recordId) {
        Long userId = LoginHelper.getUserId();
        LifeRecord record = lifeRecordMapper.selectById(recordId);
        if (record == null || !userId.equals(record.getUserId())) {
            return R.fail("日记不存在或无权限查看");
        }
        return R.ok(lifeRecordService.queryById(recordId));
    }

    /**
     * 修改日记
     */
    @PutMapping()
    public R<Void> edit(@RequestBody LifeRecordBo bo) {
        Long userId = LoginHelper.getUserId();
        LifeRecord record = lifeRecordMapper.selectById(bo.getRecordId());
        if (record == null || !userId.equals(record.getUserId())) {
            return R.fail("日记不存在或无权限修改");
        }
        return toAjax(lifeRecordService.updateByBo(bo));
    }

    /**
     * 删除日记
     */
    @DeleteMapping("/{recordId}")
    public R<Void> remove(@PathVariable Long recordId) {
        Long userId = LoginHelper.getUserId();
        LifeRecord record = lifeRecordMapper.selectById(recordId);
        if (record == null || !userId.equals(record.getUserId())) {
            return R.fail("日记不存在或无权限删除");
        }
        return toAjax(lifeRecordService.deleteWithValidByIds(List.of(recordId), true));
    }

    /**
     * 按月查询有日记的日期
     *
     * @param yearMonth 年月，格式 yyyy-MM
     */
    @GetMapping("/calendar")
    public R<List<String>> calendar(@RequestParam String yearMonth) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<LifeRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LifeRecord::getUserId, userId);
        lqw.apply("DATE_FORMAT(record_date, '%Y-%m') = {0}", yearMonth);
        lqw.select(LifeRecord::getRecordDate);
        lqw.groupBy(LifeRecord::getRecordDate);
        List<LifeRecord> list = lifeRecordMapper.selectList(lqw);
        List<String> dates = list.stream()
            .map(r -> {
                java.time.LocalDate localDate = r.getRecordDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                return localDate.toString();
            })
            .collect(Collectors.toList());
        return R.ok(dates);
    }

    /**
     * 上传文件（小程序端）
     *
     * @param file 文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file) {
        SysOssVo oss = ossService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getOssId().toString());
        return R.ok(uploadVo);
    }
}
