package com.aio.runtime.subscribe.service.impl;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.subscribe.domain.SubscribeLogBo;
import com.aio.runtime.subscribe.domain.SubscribeLogVo;
import com.aio.runtime.subscribe.domain.dao.SubscribeLogDo;
import com.aio.runtime.subscribe.domain.enums.SubscibeHandleStatusEnum;
import com.aio.runtime.subscribe.domain.params.QuerySubscribeLogParams;
import com.aio.runtime.subscribe.domain.params.UpdateSubscribeLogStatusParams;
import com.aio.runtime.subscribe.domain.properties.AioSubscribeProperties;
import com.aio.runtime.subscribe.mapper.SubscribeLogMapper;
import com.aio.runtime.subscribe.service.AbstractSubscribeLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc 订阅日志SQLlite存储方案
 * @date 2024/07/24
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = AioSubscribeProperties.PREFIX, name = "scheme", havingValue = "mysql", matchIfMissing = true)
public class MysqlSubscribeLogServiceImpl extends AbstractSubscribeLogService {
    public MysqlSubscribeLogServiceImpl(AioSubscribeProperties properties) {
        super(properties);
    }


    @Autowired
    private SubscribeLogMapper subscribeLogMapper;

    @Value("${project.workspace.path}")
    private String projectWorkspace;

    @Override
    public void batchSave(List<SubscribeLogBo> recordBos) {
        if (ObjectUtil.isEmpty(recordBos)) {
            return;
        }
        List<SubscribeLogDo> recordList = Convert.toList(SubscribeLogDo.class, recordBos);
        for (SubscribeLogDo recordDo : recordList) {
            if (StringUtils.isBlank(recordDo.getId())) {
                recordDo.setId(IdUtil.getSnowflakeNextIdStr());
            }
        }
        subscribeLogMapper.insert(recordList);

    }

    @Override
    public PageResult<SubscribeLogVo> getPage(QuerySubscribeLogParams params, KgoPage page) {
        LambdaQueryWrapper<SubscribeLogDo> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getSubscribeName())) {
            queryWrapper.like(SubscribeLogDo::getSubscribeName, params.getSubscribeName());
        }
        if (StringUtils.isNotBlank(params.getClassName())) {
            queryWrapper.like(SubscribeLogDo::getClassName, params.getClassName());
        }
        if (StringUtils.isNotBlank(params.getMessage())) {
            queryWrapper.like(SubscribeLogDo::getMessage, params.getMessage());
        }
        if (StringUtils.isNotBlank(params.getMethodName())) {
            queryWrapper.like(SubscribeLogDo::getMethodName, params.getMethodName());
        }
        if (ObjectUtil.isNotNull(params.getHandleStatus())) {
            queryWrapper.eq(SubscribeLogDo::getHandleStatus, params.getHandleStatus());
        }

        if (StringUtils.isNotBlank(params.getId())) {
            queryWrapper.eq(SubscribeLogDo::getId, params.getId());
        }

        if (StringUtils.isNotBlank(params.getUserId())) {
            queryWrapper.eq(SubscribeLogDo::getUserId, params.getUserId());
        }

        if (StringUtils.isNotBlank(params.getCompanyId())) {
            queryWrapper.eq(SubscribeLogDo::getCompanyId, params.getCompanyId());
        }

        if (ObjectUtil.isNotNull(params.getCreateFromTime())) {
            queryWrapper.ge(SubscribeLogDo::getCreateTime, new Date(params.getCreateFromTime()));
        }
        if (ObjectUtil.isNotNull(params.getCreateToTime())) {
            queryWrapper.le(SubscribeLogDo::getCreateTime, new Date(params.getCreateToTime()));
        }

        queryWrapper.orderByDesc(SubscribeLogDo::getCreateTime, SubscribeLogDo::getHandleTime);
        Page<SubscribeLogDo> pageInfo = new Page<>(page.getPageNum(), page.getPageSize());
        Page<SubscribeLogDo> reportDoPage = subscribeLogMapper.selectPage(pageInfo, queryWrapper);
        PageResult<SubscribeLogVo> pageResult = new PageResult<>(Convert.toList(SubscribeLogVo.class, reportDoPage.getRecords()), reportDoPage.getTotal());
        return pageResult;

    }

    @Override
    public void updateSubscribeLogStatusToHandled(UpdateSubscribeLogStatusParams params) {
        SubscribeLogDo subscribeLogDo = Convert.convert(SubscribeLogDo.class, params);
        subscribeLogDo.setHandleTime(new Date());
        subscribeLogDo.setHandleStatus(SubscibeHandleStatusEnum.HANDLED.getStatus());
        int i = subscribeLogMapper.updateById(subscribeLogDo);
        log.debug("订阅的错误日志，更新处理状态，更新结果： [ {} ] 。", i);
    }

}
