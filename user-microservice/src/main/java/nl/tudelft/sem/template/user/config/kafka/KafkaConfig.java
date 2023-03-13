package nl.tudelft.sem.template.user.config.kafka;

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
import sem.commons.StatusDTO;
import sem.commons.TokenDTO;
import sem.commons.UserCredentials;
import sem.commons.UserDTO;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private transient KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Producer configs map.
     *
     * @return the map
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    /**
     * Consumer configs map.
     *
     * @return the map
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, UserDTO> consumerFactoryUserDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(UserDTO.class));
    }

    @Bean
    public ProducerFactory<String, StatusDTO> producerFactoryStatusDTO() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, StatusDTO> kafkaTemplateStatusDTO() {
        return new KafkaTemplate<>(producerFactoryStatusDTO());
    }

    /**
     * Kafka listener container factory user dto concurrent kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDTO> kafkaListenerContainerFactoryUserDTO() {
        ConcurrentKafkaListenerContainerFactory<String, UserDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactoryUserDTO());

        factory.setReplyTemplate(kafkaTemplateStatusDTO());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserCredentials> consumerFactoryUserCredentials() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(UserCredentials.class));
    }

    @Bean
    public ProducerFactory<String, TokenDTO> producerFactoryTokenDTO() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template token dto kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, TokenDTO> kafkaTemplateTokenDTO() {
        return new KafkaTemplate<>(producerFactoryTokenDTO());
    }

    /**
     * Kafka listener container factory user credentials concurrent kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCredentials> kafkaListenerContainerFactoryUserCredentials() {
        ConcurrentKafkaListenerContainerFactory<String, UserCredentials> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactoryUserCredentials());

        factory.setReplyTemplate(kafkaTemplateTokenDTO());

        return factory;
    }

    //TODO: REMOVE THESE CONFIGS ONCE THE EXAMPLES ARE DELETED
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}
