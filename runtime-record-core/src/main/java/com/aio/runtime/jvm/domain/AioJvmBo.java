package com.aio.runtime.jvm.domain;

import com.aio.runtime.db.orm.annotations.AioField;
import com.aio.runtime.db.orm.annotations.AioId;
import com.aio.runtime.db.orm.annotations.AioTable;
import com.aio.runtime.db.orm.domain.enums.AioDbTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author lzm
 * @desc 运行时
 * @date 2024/08/16
 */
@Data
@AioTable(value = "aio_jvm_statistics",type = AioDbTypeEnum.SQL_LITE)
public class AioJvmBo {
    @AioId()
    private String id;
    /**
     * jvm 最大运行的内存
     */
    @AioField("jvm_memory_max")
    private String jvmMemoryMax;
    /**
     * jvm 使用的内存
     */
    @AioField("jvm_memory_used")
    private String jvmMemoryUsed;
    /**
     *  JVM 中已经分配的内存量
     */
    @AioField("jvm_memory_committed")
    private String jvmMemoryCommitted;

    /**
     * 守护线程数量
     */
    @AioField("jvm_threads_daemon")
    private String jvmThreadsDaemon;

    /**
     * jvm 线程最多的时候的数量
     */
    @AioField("jvm_threads_peak")
    private String jvmThreadsPeak;

    /**
     * jvm 获取线程数量
     */
    @AioField("jvm_threads_live")
    private String jvmThreadsLive;

    @AioField("create_time")
    private Date createTime;
}
