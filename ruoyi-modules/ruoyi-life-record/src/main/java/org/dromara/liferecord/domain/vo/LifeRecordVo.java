package org.dromara.liferecord.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.dromara.liferecord.domain.LifeRecord;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 日记视图对象 life_record
 *
 * @author Lion Li
 * @date 2026-05-21
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = LifeRecord.class)
public class LifeRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日记ID
     */
    @ExcelProperty(value = "日记ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recordId;

    /**
     * 日记日期
     */
    @ExcelProperty(value = "日记日期")
    private Date recordDate;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 心情标签（开心/平静/难过/愤怒）
     */
    @ExcelProperty(value = "心情标签", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "开=心/平静/难过/愤怒")
    private String mood;

    /**
     * 天气
     */
    @ExcelProperty(value = "天气")
    private String weather;

    /**
     * 地点
     */
    @ExcelProperty(value = "地点")
    private String location;

    /**
     * 状态（0正常 1下架）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=下架")
    private Long status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
