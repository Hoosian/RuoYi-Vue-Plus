package org.dromara.liferecord.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 日记对象 life_record
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("life_record")
public class LifeRecord extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日记ID
     */
    @TableId(value = "record_id")
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
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
