package nl.tudelft.sem.template.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(
            topics = "example",
            groupId = "foo",
            containerFactory = "kafkaListenerContainerFactory2"
    )
    void listener(String data) {
        System.out.println("\nYou said " + data + ".\n Hello Producer! I am the listener");
    }
}
