package com.aio.runtime.jvm.domain;

import io.micrometer.core.instrument.Statistic;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;

import java.util.List;

/**
 * @author lzm
 * @desc 运行时
 * @date 2024/08/16
 */
@Data
public class AioJvmCollectBo extends AioJvmBo {

    public void collect(MetricsEndpoint.MetricResponse metric){
        jvmMemoryUsedCollect(metric);
        jvmMemoryMaxCollect(metric);
        jvmMemoryCommittedCollect(metric);
        jvmThreadsDaemonCollect(metric);
        jvmThreadsLiveCollect(metric);
        jvmThreadsPeakCollect(metric);

    }
    private void jvmMemoryUsedCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.memory.used",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                Double value = measurement.getValue();
                value = value / (1024.0 * 1024.0);
                setJvmMemoryUsed(String.format("%.4f", value));
            }
        }
    }

    private void jvmMemoryMaxCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.memory.max",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                Double value = measurement.getValue();
                value = value / (1024.0 * 1024.0);
                setJvmMemoryMax(String.format("%.4f", value));
            }
        }
    }

    private void jvmMemoryCommittedCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.memory.committed",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                Double value = measurement.getValue();
                value = value / (1024.0 * 1024.0);
                setJvmMemoryCommitted(String.format("%.4f", value));
            }
        }
    }

    /**
     * 守护线程数量
     * @param metric
     */

    private void jvmThreadsDaemonCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.threads.daemon",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                setJvmThreadsDaemon(String.format("%.0f", measurement.getValue()));
            }
        }
    }

    private void jvmThreadsLiveCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.threads.live",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                setJvmThreadsLive(String.format("%.0f", measurement.getValue()));
            }
        }
    }

    private void jvmThreadsPeakCollect(MetricsEndpoint.MetricResponse metric){
        if (!StringUtils.equals("jvm.threads.peak",metric.getName())){
            return;
        }
        List<MetricsEndpoint.Sample> measurements = metric.getMeasurements();
        for (MetricsEndpoint.Sample measurement : measurements) {
            Statistic statistic = measurement.getStatistic();
            if (StringUtils.equalsIgnoreCase(statistic.name(),"VALUE")){
                setJvmThreadsPeak(String.format("%.0f", measurement.getValue()));
            }
        }
    }
}
