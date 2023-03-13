package nl.tudelft.sem.template.kafka.example;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.ExampleUser;


@RestController
@RequiredArgsConstructor
public class ExampleController {

    @KafkaListener(
            topics = "example-topic",
            clientIdPrefix = "json",
            containerFactory = "kafkaListenerContainerFactory1")
    public void consume(ConsumerRecord<String, ExampleUser> record,
                        @Payload ExampleUser payload) {
        System.out.println("Registered a user with netId: " + payload.getNetId());
    }
}