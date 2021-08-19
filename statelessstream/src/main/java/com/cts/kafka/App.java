package com.cts.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RetriableCommitFailedException;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple main application
 */
@Slf4j
public class App {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-stateless-wordcount-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> kStream = builder.stream("word-count-input");

        KStream<String, String> lowerCasekStream = kStream.map((key, value) -> {
            return KeyValue.pair(key == null ? null : key.toLowerCase(), value.toLowerCase());
        });

        KStream<String, String> splittedBySpacekStream = lowerCasekStream.flatMap((key, value) -> {
            ArrayList<KeyValue<String, String>> result = new ArrayList<>();
            Arrays.asList(value.split(" ")).forEach(item -> result.add(KeyValue.pair(key, item)));
            return result;
        });

        KStream<String, String> valueTurnedToKeyStream = splittedBySpacekStream.selectKey((key, value) -> {
            log.info("Inside  splittedBySpacekStream::: key::" + key + " Value:::" + value);
            return value;
        });

        // valueTurnedToKeyStream.peek((key,value)->log.info("KEY:: "+key+"
        // VALUE::"+value));

        KTable<String, Long> wordCounTable = valueTurnedToKeyStream.groupByKey().count();
        wordCounTable.toStream().peek((key, value) -> log.info("KEY:: " + key + " VALUE::" + value));

        wordCounTable.toStream().map((key, value) -> KeyValue.pair(key, value.toString())).to("word-count-output",
                Produced.with(Serdes.String(), Serdes.String()));
        // wordCount.to(Serdes.String(),Serdes.Long(),"word-count-output");
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.cleanUp(); // only do this in dev - not in prod
        // streams.start();

        // print the topology
        log.info(streams.toString());
        log.info(topology.describe().toString());

        // shutdown hook to correctly close the streams application
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach a shutdown handler to catch control-c and terminate the application
        // gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-stateless-wordcount-app") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

}
