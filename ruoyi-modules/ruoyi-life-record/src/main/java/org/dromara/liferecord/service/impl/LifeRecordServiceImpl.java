package org.dromara.liferecord.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.liferecord.domain.bo.LifeRecordBo;
import org.dromara.liferecord.domain.vo.LifeRecordVo;
import org.dromara.liferecord.domain.LifeRecord;
import org.dromara.liferecord.mapper.LifeRecordMapper;
import org.dromara.liferecord.service.ILifeRecordService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 日记Service业务层处理
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LifeRecordServiceImpl implements ILifeRecordService {

    private final LifeRecordMapper baseMapper;

    /**
     * 查询日记
     *
     * @param recordId 主键
     * @return 日记
     */
    @Override
    public LifeRecordVo queryById(Long recordId){
        return baseMapper.selectVoById(recordId);
    }

    /**
     * 分页查询日记列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 日记分页列表
     */
    @Override
    public TableDataInfo<LifeRecordVo> queryPageList(LifeRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<LifeRecord> lqw = buildQueryWrapper(bo);
        Page<LifeRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的日记列表
     *
     * @param bo 查询条件
     * @return 日记列表
     */
    @Override
    public List<LifeRecordVo> queryList(LifeRecordBo bo) {
        LambdaQueryWrapper<LifeRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<LifeRecord> buildQueryWrapper(LifeRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<LifeRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(LifeRecord::getRecordId);
        lqw.eq(bo.getRecordDate() != null, LifeRecord::getRecordDate, bo.getRecordDate());
        lqw.eq(StringUtils.isNotBlank(bo.getMood()), LifeRecord::getMood, bo.getMood());
        lqw.eq(StringUtils.isNotBlank(bo.getWeather()), LifeRecord::getWeather, bo.getWeather());
        lqw.eq(StringUtils.isNotBlank(bo.getLocation()), LifeRecord::getLocation, bo.getLocation());
        lqw.eq(bo.getStatus() != null, LifeRecord::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增日记
     *
     * @param bo 日记
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(LifeRecordBo bo) {
        LifeRecord add = MapstructUtils.convert(bo, LifeRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRecordId(add.getRecordId());
        }
        return flag;
    }

    /**
     * 修改日记
     *
     * @param bo 日记
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(LifeRecordBo bo) {
        LifeRecord update = MapstructUtils.convert(bo, LifeRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(LifeRecord entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 修改日记状态（下架/恢复）
     *
     * @param recordId 日记ID
     * @param status   状态（0正常 1下架）
     * @return 是否修改成功
     */
    @Override
    public Boolean updateStatus(Long recordId, Long status) {
        LifeRecord update = new LifeRecord();
        update.setRecordId(recordId);
        update.setStatus(status);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 校验并批量删除日记信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
