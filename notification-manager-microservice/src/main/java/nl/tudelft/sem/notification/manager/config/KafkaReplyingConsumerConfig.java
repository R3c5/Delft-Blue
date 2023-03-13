package nl.tudelft.sem.notification.manager.config;

import lombok.Getter;
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
import sem.commons.NetIdDTO;
import sem.commons.NotificationPackage;
import sem.commons.PendingRequestsDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * This class has all the configurations that allow for the consuming and replying of messages. There is some
 * duplication with the ProducerConfiguration and ConsumerConfiguration classes but this way it improves readability
 * and understanding of what is needed to consume and produce messages
 */
@EnableKafka
@Configuration
@Getter
public class KafkaReplyingConsumerConfig {

    private KafkaProperties kafkaProperties;

    public KafkaReplyingConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * This method will return the producerConfiguration to be used in publishing messages.
     * @return the configs
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    /**
     * This method will return the consumer configs to be later used in consuming messages from topics.
     * @return the configs
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, NetIdDTO> consumerFactoryNetId() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(NetIdDTO.class));
    }

    /**
     * This method will set up the container to allow other methods to listen and consume messsages, in a replying
     * fashion.
     * @return the listenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NetIdDTO> kafkaListenerContainerFactoryNetId() {
        ConcurrentKafkaListenerContainerFactory<String, NetIdDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryNetId());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplatePendingNotifications());

        return factory;
    }

    @Bean
    public KafkaTemplate<String, NotificationPackage> kafkaTemplatePendingNotifications() {
        return new KafkaTemplate<>(producerFactoryPendingNotifications());
    }

    @Bean
    public ProducerFactory<String, NotificationPackage> producerFactoryPendingNotifications() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
}
