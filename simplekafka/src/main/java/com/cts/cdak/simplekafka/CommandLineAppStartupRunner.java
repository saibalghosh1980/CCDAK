package com.cts.cdak.simplekafka;

import com.cts.cdak.simplekafka.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    @Qualifier("springManagedProducer")
    Producer kafkaProducer;
    @Override
    public void run(String... args) throws Exception {
        for (int iCounter=0;iCounter<30;iCounter++){
            Thread.sleep(2000);
            kafkaProducer.sendMessage("Message number::"+iCounter+" and at time ::"+new Date().getTime());
        }
    }
}
