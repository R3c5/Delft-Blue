package sem.faculty.config.kafka;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
import sem.commons.*;


/**
 * The type Kafka config.
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    private transient KafkaProperties kafkaProperties;

    /**
     * Instantiates a new Kafka config.
     *
     * @param kafkaProperties the kafka properties
     */
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
    public Map<String, Object> consumerConfigsKafka() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    /**
     * Consumer factory faculty name consumer factory.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, FacultyNameDTO> consumerFactoryFacultyName() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsKafka(),
                new StringDeserializer(), new JsonDeserializer<>(FacultyNameDTO.class));
    }

    /**
     * Kafka listener container factory faculty name concurrent kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FacultyNameDTO> kafkaListenerContainerFactoryFacultyName() {
        ConcurrentKafkaListenerContainerFactory<String, FacultyNameDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryFacultyName());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplatePendingRequests());

        return factory;
    }

    /**
     * Kafka template pending requests kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, PendingRequestsDTO> kafkaTemplatePendingRequests() {
        return new KafkaTemplate<>(producerFactoryPendingRequests());
    }

    /**
     * Producer factory producer factory.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, PendingRequestsDTO> producerFactoryPendingRequests() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }


    /**
     * Consumer factory accept requests consumer factory.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, AcceptRequestsDTO> consumerFactoryAcceptRequests() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsKafka(),
                new StringDeserializer(), new JsonDeserializer<>(AcceptRequestsDTO.class));
    }

    /**
     * Kafka listener container factory accept requests concurrent kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AcceptRequestsDTO> kafkaListenerContainerFactoryAcceptRequests() {
        ConcurrentKafkaListenerContainerFactory<String, AcceptRequestsDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryAcceptRequests());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateStatus());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, DateDTO> consumerFactoryDateDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsKafka(),
                new StringDeserializer(), new JsonDeserializer<>(DateDTO.class));
    }

    /**
     * Kafka listener container factory for DateDTO.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DateDTO> kafkaListenerContainerFactoryDateDTO() {
        ConcurrentKafkaListenerContainerFactory<String, DateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryDateDTO());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateSysadminView());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, RequestDTO> consumerFactoryRequestDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsKafka(),
                new StringDeserializer(), new JsonDeserializer<>(RequestDTO.class));
    }

    /**
     * Kafka listener container factory for RequestDTO.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestDTO> kafkaListenerContainerFactoryRequestDTO() {
        ConcurrentKafkaListenerContainerFactory<String, RequestDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryRequestDTO());

        factory.setReplyTemplate(kafkaTemplateStatus());

        return factory;
    }

    /**
     * Kafka template status kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, StatusDTO> kafkaTemplateStatus() {
        return new KafkaTemplate<>(producerFactoryStatus());
    }

    /**
     * Producer factory status producer factory.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, StatusDTO> producerFactoryStatus() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }


    /**
     * The Consumer Factory for date received as response.
     * @return the consumerFactory
     */
    @Bean
    public ConsumerFactory<String, LocalDate> consumerFactoryDate() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsKafka(),
                new StringDeserializer(), new JsonDeserializer<>(LocalDate.class));
    }


    /**
     * Producer factory.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, ScheduleDateDTO> producerFactoryScheduleDateDTO() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, ScheduleDateDTO> kafkaTemplateScheduleDateDTO() {
        return new KafkaTemplate<>(producerFactoryScheduleDateDTO());
    }

    @Bean
    public ProducerFactory<String, SysadminScheduleDTO> producerFactorySysadminView() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, SysadminScheduleDTO> kafkaTemplateSysadminView() {
        return new KafkaTemplate<>(producerFactorySysadminView());
    }

    @Bean
    public ProducerFactory<String, NotificationDTO> producerFactoryNotificationDTO() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, NotificationDTO> kafkaTemplateNotificationDTO() {
        return new KafkaTemplate<>(producerFactoryNotificationDTO());
    }

}
