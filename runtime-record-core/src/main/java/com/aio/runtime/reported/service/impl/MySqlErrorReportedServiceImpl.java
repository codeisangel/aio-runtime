package com.aio.runtime.reported.service.impl;

import cn.aio1024.framework.basic.domain.page.KgoPage;
import cn.aio1024.framework.basic.domain.page.PageResult;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aio.runtime.reported.domain.ErrorReportedProperties;
import com.aio.runtime.reported.domain.dao.ErrorReportDo;
import com.aio.runtime.reported.domain.params.QueryErrorParams;
import com.aio.runtime.reported.mapper.ErrorReportedMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author lzm
 * @desc 错误上报服务
 * @date 2024/10/26
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = ErrorReportedProperties.PREFIX, value = "scheme", havingValue = "mysql", matchIfMissing = true)
public class MySqlErrorReportedServiceImpl extends AbstractErrorReportedServiceImpl {
    @Autowired
    private ErrorReportedMapper reportedMapper;
    private LambdaQueryWrapper<ErrorReportDo> queryWrapper(QueryErrorParams params){
        LambdaQueryWrapper<ErrorReportDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ErrorReportDo::getCreateTime);
        if (ObjectUtil.isNotNull(params.getCreateFromTime())){
            queryWrapper.ge(ErrorReportDo::getCreateTime,new Date(params.getCreateFromTime()));
        }
        if (ObjectUtil.isNotNull(params.getCreateToTime())){
            queryWrapper.le(ErrorReportDo::getCreateTime,new Date(params.getCreateToTime()));
        }
        if (StringUtils.isNotBlank(params.getPlatform())) {
            queryWrapper.like(ErrorReportDo::getPlatform,params.getPlatform());
        }
        if (StringUtils.isNotBlank(params.getRemark())) {
            queryWrapper.like(ErrorReportDo::getRemark,params.getRemark());
        }
        if (StringUtils.isNotBlank(params.getMessage())) {
            queryWrapper.like(ErrorReportDo::getMessage,params.getMessage());
        }
        if (StringUtils.isNotBlank(params.getTerminal())) {
            queryWrapper.like(ErrorReportDo::getTerminal,params.getTerminal());
        }
        if (StringUtils.isNotBlank(params.getApiUrl())) {
            queryWrapper.like(ErrorReportDo::getApiUrl,params.getApiUrl());
        }
        if (StringUtils.isNotBlank(params.getLevel())) {
            queryWrapper.eq(ErrorReportDo::getLevel,params.getLevel());
        }
        if (StringUtils.isNotBlank(params.getType())) {
            queryWrapper.eq(ErrorReportDo::getType,params.getType());
        }
        return queryWrapper;
    }
    @Override
    public PageResult getPage(QueryErrorParams params, KgoPage page) {
        LambdaQueryWrapper<ErrorReportDo> queryWrapper = queryWrapper(params);
        Page<ErrorReportDo> pageInfo = new Page<>(page.getPageNum(),page.getPageSize());
        Page<ErrorReportDo> reportDoPage = reportedMapper.selectPage(pageInfo, queryWrapper);
        PageResult<ErrorReportDo> pageResult = new PageResult<>(reportDoPage.getRecords(),reportDoPage.getTotal());
        return pageResult;
    }

    @Override
    public void batchSaveError(List<ErrorReportDo> recordList) {
        if (ObjectUtil.isEmpty(recordList)) {
            return;
        }
        recordList.stream().forEach(reportDo -> {
            if (StringUtils.isBlank(reportDo.getId())){
                reportDo.setId(IdUtil.getSnowflakeNextIdStr());
            }
        });
        reportedMapper.insert(recordList);
    }

}
