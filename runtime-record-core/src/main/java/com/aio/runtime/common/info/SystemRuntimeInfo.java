package com.aio.runtime.common.info;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Date;

/**
 * @author lzm
 * @desc 系统运行时信息
 * @date 2024/08/16
 */

public class SystemRuntimeInfo {
    private static Date SYSTEM_STARTING_TIME;
    private static Date SYSTEM_STARTED_TIME;

    public static void systemStarted(){
        SYSTEM_STARTED_TIME = new Date();
    }
    public static void systemStarting(){
        SYSTEM_STARTING_TIME = new Date();
    }
    public static String getSystemStartingTime(){
        if (ObjectUtil.isNull(SYSTEM_STARTING_TIME)){
            return null;
        }
        return DateUtil.formatDateTime(SYSTEM_STARTING_TIME);
    }

    public static String getSystemStartedTime(){
        if (ObjectUtil.isNull(SYSTEM_STARTED_TIME)){
            return null;
        }
        return DateUtil.formatDateTime(SYSTEM_STARTED_TIME);
    }
}
