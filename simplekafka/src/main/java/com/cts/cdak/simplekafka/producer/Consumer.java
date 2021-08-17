package com.cts.cdak.simplekafka.producer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("springManagedConsumer")
public class Consumer {

    @KafkaListener(id = "test", topics = "skg-topic-1")
    public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("["+partition+"] - Message recieved::" + message+" Key ::"+key);
    }

}
