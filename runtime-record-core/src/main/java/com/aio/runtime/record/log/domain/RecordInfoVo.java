package com.aio.runtime.record.log.domain;

import lombok.Data;

import java.text.DecimalFormat;

/**
 * @author lzm
 * @desc 记录的详细信息
 * @date 2024/06/06
 */
@Data
public class RecordInfoVo {
    private String size;
    public void setSize(long sizeNum){
        DecimalFormat df = new DecimalFormat("#.000");
        String formattedNumber = df.format((double)sizeNum/1024);
        this.size = String.format("%s MB",formattedNumber);
    }
}
