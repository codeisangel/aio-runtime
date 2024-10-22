package com.aio.runtime.reported.domain.params;

import lombok.Data;

/**
 * @author lzm
 * @desc 错误上报参数
 * @date 2024/10/17
 */
@Data
public class AddErrorParams {
    /**
     * 错误级别，info (信息),warn （警告）,error （错误）,fatal（灾难）
     */
    private String level;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 追踪码
     */
    private String traceId;
    /**
     * 错误类型
     */
    private String type;
    /**
     * 错误接口地址
     */
    private String apiUrl;
    /**
     * 非必填，如果有，系统会记录用户信息
     */
    private String token;
    /**
     * 终端。web，PC ， Android，ios，微信小程序，H5，微信公众号 等
     */
    private String terminal;
    /**
     * 终端信息。例如：浏览器版本，系统版本，设备型号等
     */
    private String terminalInfo;
    /**
     * 项目平台。例如 车管业务的 车主端，金融机构端
     */
    private String platform;
    /**
     * 备注
     */
    private String remark;
}
