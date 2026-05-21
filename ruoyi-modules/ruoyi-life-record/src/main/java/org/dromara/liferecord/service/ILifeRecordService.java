package org.dromara.liferecord.service;

import org.dromara.liferecord.domain.vo.LifeRecordVo;
import org.dromara.liferecord.domain.bo.LifeRecordBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 日记Service接口
 *
 * @author Lion Li
 * @date 2026-05-21
 */
public interface ILifeRecordService {

    /**
     * 查询日记
     *
     * @param recordId 主键
     * @return 日记
     */
    LifeRecordVo queryById(Long recordId);

    /**
     * 分页查询日记列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 日记分页列表
     */
    TableDataInfo<LifeRecordVo> queryPageList(LifeRecordBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的日记列表
     *
     * @param bo 查询条件
     * @return 日记列表
     */
    List<LifeRecordVo> queryList(LifeRecordBo bo);

    /**
     * 新增日记
     *
     * @param bo 日记
     * @return 是否新增成功
     */
    Boolean insertByBo(LifeRecordBo bo);

    /**
     * 修改日记
     *
     * @param bo 日记
     * @return 是否修改成功
     */
    Boolean updateByBo(LifeRecordBo bo);

    /**
     * 校验并批量删除日记信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
