package com.aio.runtime.subscribe.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.aio.runtime.subscribe.domain.SubscribeLogBo;
import com.aio.runtime.subscribe.domain.properties.AioSubscribeProperties;
import com.aio.runtime.subscribe.domain.properties.SubscribeFeiShuProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lzm
 * @desc 订阅日志服务抽象类
 * @date 2024/07/24
 */
public abstract class AbstractSubscribeLogService implements SubscribeLogService {
    private final AioSubscribeProperties subscribeProperties;
    private boolean notifyEnable = false;
    private static BlockingQueue<SubscribeLogBo> RECORD_QUEUE = new LinkedBlockingQueue<>(10000);;
    public static void addRecord(SubscribeLogBo logBo){
        RECORD_QUEUE.add(logBo);
    }
    private ScheduledExecutorService executorSubscribeLogService = Executors.newScheduledThreadPool(2);
    public AbstractSubscribeLogService(AioSubscribeProperties properties){
        this.subscribeProperties = properties;
        judgeStartNotify();
        Runnable task = () -> {
            List<SubscribeLogBo> recordBos = drainTo();
            batchNotify(recordBos);
            batchSave(recordBos);
        };
        executorSubscribeLogService.scheduleAtFixedRate(task, 30, 10, TimeUnit.SECONDS);
    }
    private List<SubscribeLogBo> drainTo() {
        List<SubscribeLogBo> records = new ArrayList<>();
        RECORD_QUEUE.drainTo(records);
        return records;
    }

    private void judgeStartNotify(){
        if (ObjectUtil.isEmpty(subscribeProperties)){
            return;
        }
        SubscribeFeiShuProperties feishu = subscribeProperties.getFeishu();
        if (ObjectUtil.isEmpty(feishu)){
            return;
        }
        if (StringUtils.isBlank(feishu.getWebhook())){
            return;
        }
        notifyEnable = true;
    }
    /**
     * 批量通知
     * @param records
     */
    private void batchNotify(List<SubscribeLogBo> records){
        if (!notifyEnable){
            return;
        }
        if (ObjectUtil.isEmpty(records)){
            return;
        }
        for (SubscribeLogBo record : records) {
            notify(record);
        }
    }
    private void notify(SubscribeLogBo record){
        if (ObjectUtil.isEmpty(record)){
            return;
        }
        // 如果订阅主题里面不包含当前主题就直接返回
        if (!subscribeProperties.getTopics().contains(record.getSubscribeName())) {
            return;
        }
        feiShuNotify(record);
    }
    private void feiShuNotify(SubscribeLogBo record){
        SubscribeFeiShuProperties feishu = subscribeProperties.getFeishu();
        try {
            ClassPathResource resource = new ClassPathResource("notify/feishu/subscribeLog.json");
            InputStream stream = resource.getInputStream();
            String contentStr = IoUtil.readUtf8(stream);
            JSONObject content = JSONObject.parseObject(contentStr);
            JSONPath.set(content,"post.zh_cn.title",String.format("订阅[ %s ]日志",record.getSubscribeName()));

            JSONPath.set(content,"post.zh_cn.content[0][0].text",record.getMessage());

            String legalText = StrUtil.format("发生时间 ：{} ", DateUtil.formatDateTime(record.getCreateTime()));
            JSONPath.set(content,"post.zh_cn.content[2][0].text",legalText);


            JSONObject body = new JSONObject();
            body.put("msg_type","post");
            body.put("content",content);
            HttpResponse execute = HttpRequest.post(feishu.getWebhook())
                    .header("Content-Type", "application/json")
                    .body(body.toJSONString()).execute();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public abstract void batchSave(List<SubscribeLogBo> recordBos);
}
