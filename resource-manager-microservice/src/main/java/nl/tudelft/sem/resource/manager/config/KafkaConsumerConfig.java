package nl.tudelft.sem.resource.manager.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import sem.commons.ClusterNodeDTO;
import sem.commons.ScheduleDateDTO;

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
     * This method will return the consumer properties to be used.
     * @return the Map that represents the consumer properties
     */
    @Bean
    public Map<String, Object> consumerProperties() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    /**
     * This method will set the {@link ConsumerFactory} so we can consume messages in general.
     * It should be used by the ListenerFactory, so we can consume messages
     * through the {@link org.springframework.kafka.annotation.KafkaListener KafkaListener} annotation.
     *
     * @return the {@link ConsumerFactory}
     */
    @Bean
    public ConsumerFactory<String, ScheduleDateDTO> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties(),
                new StringDeserializer(),
                new JsonDeserializer<>(ScheduleDateDTO.class));
    }

    /**
     * This method will create the factory in order to build the ListenerContainer so we can use the @KafkaListener
     * annotation.
     * @return the KafkaListenerContainer
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ScheduleDateDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ScheduleDateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
