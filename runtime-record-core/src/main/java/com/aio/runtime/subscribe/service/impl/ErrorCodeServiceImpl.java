package com.aio.runtime.subscribe.service.impl;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.convert.Convert;
import com.aio.runtime.subscribe.domain.bo.ErrorCodeBo;
import com.aio.runtime.subscribe.domain.dao.ErrorCodeDo;
import com.aio.runtime.subscribe.domain.params.AddErrorCodeParams;
import com.aio.runtime.subscribe.domain.params.QueryErrorCodeParams;
import com.aio.runtime.subscribe.domain.properties.AioSubscribeProperties;
import com.aio.runtime.subscribe.mapper.ErrorCodeMapper;
import com.aio.runtime.subscribe.service.ErrorCodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author lzm
 * @desc 错误码服务
 * @date 2025/04/08
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = AioSubscribeProperties.PREFIX, name = "scheme", havingValue = "mysql", matchIfMissing = true)
public class ErrorCodeServiceImpl implements ErrorCodeService {
    @Autowired
    private ErrorCodeMapper errorCodeMapper;
    @Override
    public PageResult<ErrorCodeBo> getPage(QueryErrorCodeParams params, KgoPage page) {
        LambdaQueryWrapper<ErrorCodeDo> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getId())) {
            queryWrapper.like(ErrorCodeDo::getId, params.getId());
        }
        if (StringUtils.isNotBlank(params.getContent())) {
            queryWrapper.like(ErrorCodeDo::getContent, params.getContent());
        }
        if (StringUtils.isNotBlank(params.getModule())) {
            queryWrapper.like(ErrorCodeDo::getModule, params.getModule());
        }
        if (StringUtils.isNotBlank(params.getSolution())) {
            queryWrapper.like(ErrorCodeDo::getSolution, params.getSolution());
        }
        queryWrapper.orderByDesc( ErrorCodeDo::getUpdateTime,ErrorCodeDo::getCreateTime);
        Page<ErrorCodeDo> pageInfo = new Page<>(page.getPageNum(), page.getPageSize());
        Page<ErrorCodeDo> reportDoPage = errorCodeMapper.selectPage(pageInfo, queryWrapper);
        PageResult<ErrorCodeBo> pageResult = new PageResult<>(Convert.toList(ErrorCodeBo.class, reportDoPage.getRecords()), reportDoPage.getTotal());
        return pageResult;
    }

    @Override
    public void addErrorCode(AddErrorCodeParams params) {
        Assert.notNull(params, "参数不能为空");
        Assert.hasText(params.getId(), "错误码不能为空");
        Assert.hasText(params.getContent(), "错误码内容不能为空");
        Assert.hasText(params.getModule(), "模块不能为空");
        Assert.hasText(params.getSolution(), "解决方案不能为空");
        ErrorCodeDo addErrorCodeDo = Convert.convert(ErrorCodeDo.class, params);
        addErrorCodeDo.setCreateTime(new Date());
        addErrorCodeDo.setUpdateTime(new Date());
        int insert = errorCodeMapper.insert(addErrorCodeDo);
        log.info("创建错误码: {} ,结果：{} ",addErrorCodeDo,insert);
    }

    @Override
    public void updateErrorCode(ErrorCodeBo params) {
        Assert.notNull(params, "参数不能为空");
        Assert.hasText(params.getId(), "错误码不能为空");
        ErrorCodeDo addErrorCodeDo = Convert.convert(ErrorCodeDo.class, params);
        addErrorCodeDo.setUpdateTime(new Date());
        int insert = errorCodeMapper.updateById(addErrorCodeDo);
        log.info("更新错误码: {} ,结果：{} ",addErrorCodeDo,insert);
    }

    @Override
    public void deleteErrorCode(String id) {
        int i = errorCodeMapper.deleteById(id);
        log.info("删除错误码[ {} ] 结果：{} ",id,i);
    }
}
