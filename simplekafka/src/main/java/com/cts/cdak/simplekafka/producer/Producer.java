package com.cts.cdak.simplekafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service("springManagedProducer")
public class Producer {

    //private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "skg-topic-1";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        log.info(String.format("#### -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), message);

    }

    public void sendMessage(int partition,String message) {
        log.info(String.format("#### -> Producing message -> %s and partition -> %s", message,partition));
        this.kafkaTemplate.send(TOPIC,partition, UUID.randomUUID().toString(), message);

    }
}
