package com.lsd.fun.modules.kafka;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 由 Canal 监听 MySQL 并发消息到 Kafka，由此监听器消费消息并更新到 ES 或 Hive
 *
 * Created by lsd
 * 2020-01-31 10:49
 */
@Slf4j
@Component
public class CanalMessageListener {

    public final static String CANAL_TOPIC = "example";

    @Autowired
    private Gson gson;

    @KafkaListener(topics = CANAL_TOPIC)
    public void handlerMessage(Object content) {
        // parse message
        log.error("收到消息：{}", gson.toJson(content));
    }

}
