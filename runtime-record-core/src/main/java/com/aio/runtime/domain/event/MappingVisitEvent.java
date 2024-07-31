package com.aio.runtime.domain.event;

import com.aio.runtime.mappings.domain.AioMappingBo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lzm
 * @desc 接口访问事件
 * @date 2024/07/29
 */
@Getter
public class MappingVisitEvent extends ApplicationEvent {
    private AioMappingBo mappingBo;
    public MappingVisitEvent(Object source,AioMappingBo mappingBo) {
        super(source);
        this.mappingBo = mappingBo;
    }
}
