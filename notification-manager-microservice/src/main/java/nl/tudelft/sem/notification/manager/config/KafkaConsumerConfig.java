package nl.tudelft.sem.notification.manager.config;


import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.notification.manager.domain.notification.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import sem.commons.FacultyNameDTO;
import sem.commons.NotificationDTO;
import sem.commons.PendingRequestsDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Getter
@Setter
public class KafkaConsumerConfig {


    private final KafkaProperties kafkaProperties;

    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }


    /**
     * This method will set the consumer factory so we can consume messages in general. It should be used by the
     * ListenerFactory so we can consume messages through the @KafkaListener annotation.
     * @return the ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, NotificationDTO> consumerFactoryNotification() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(NotificationDTO.class));
    }

    /**
     * This method will create the factory in order to build the ListenerContainer so we can use the @KafkaListener
     * annotation.
     * @return the KafkaListenerContainer
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationDTO> kafkaListenerContainerFactoryNotification() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryNotification());
        return factory;
    }

}
