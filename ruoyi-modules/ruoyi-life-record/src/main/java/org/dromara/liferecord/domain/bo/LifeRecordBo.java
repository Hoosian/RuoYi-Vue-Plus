package org.dromara.liferecord.domain.bo;

import org.dromara.liferecord.domain.LifeRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 日记业务对象 life_record
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = LifeRecord.class, reverseConvertGenerate = false)
public class LifeRecordBo extends BaseEntity {

    /**
     * 日记ID
     */
    private Long recordId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 日记日期
     */
    private Date recordDate;

    /**
     * 日记内容
     */
    @NotBlank(message = "日记内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 心情标签（开心/平静/难过/愤怒）
     */
    private String mood;

    /**
     * 天气
     */
    private String weather;

    /**
     * 地点
     */
    private String location;

    /**
     * 状态（0正常 1下架）
     */
    private Long status;

    /**
     * 备注
     */
    private String remark;


}
