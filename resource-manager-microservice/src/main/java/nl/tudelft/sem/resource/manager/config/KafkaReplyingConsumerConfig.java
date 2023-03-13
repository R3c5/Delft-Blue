package nl.tudelft.sem.resource.manager.config;

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

import java.util.HashMap;
import java.util.Map;

/**
 * This class has all the configurations that allow for the consuming and replying of messages. There is some
 * duplication with the ProducerConfiguration and ConsumerConfiguration classes but this way it improves readability
 * and understanding of what is needed to consume and produce messages
 */

@EnableKafka
@Configuration
public class KafkaReplyingConsumerConfig {

    private transient KafkaProperties kafkaProperties;

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

    /**
     * This method will return the factory for consumers of FacultyNamePackageDTO.
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, FacultyNamePackageDTO> consumerFactoryFacultyNamePackageDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(FacultyNamePackageDTO.class));
    }

    /**
     * This method will return the kafkaListener so that we can listen to messages with the @KafkaListener annotation.
     * @return the concurrentListenerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FacultyNamePackageDTO>
        kafkaListenerContainerFactoryFacultyNamePackageDTO() {
        ConcurrentKafkaListenerContainerFactory<String, FacultyNamePackageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryFacultyNamePackageDTO());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateUserView());

        return factory;
    }

    @Bean
    public KafkaTemplate<String, RegularUserView> kafkaTemplateUserView() {
        return new KafkaTemplate<>(producerFactoryUserView());
    }

    @Bean
    public ProducerFactory<String, RegularUserView> producerFactoryUserView() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ConsumerFactory<String, ClusterNodeDTO> consumerFactoryClusterNodeDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(ClusterNodeDTO.class));
    }

    /**
     * This method will return the kafkaListener so that we can listen to messages with the @KafkaListener annotation.
     * @return the concurrentListenerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClusterNodeDTO>
        kafkaListenerContainerFactoryClusterNodeDTO() {
        ConcurrentKafkaListenerContainerFactory<String, ClusterNodeDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryClusterNodeDTO());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateString());

        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        return new KafkaTemplate<>(producerFactoryString());
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ConsumerFactory<String, TokenDTO> consumerFactoryTokenDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(TokenDTO.class));
    }

    /**
     * This method will return the kafkaListener so that we can listen to messages with the @KafkaListener annotation.
     * @return the concurrentListenerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TokenDTO> kafkaListenerContainerFactoryTokenDTO() {
        ConcurrentKafkaListenerContainerFactory<String, TokenDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryTokenDTO());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateString());

        return factory;
    }

    /**
     * kafkaListener for ScheduleDateDTO.
     * @return the ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ScheduleDateDTO> kafkaListenerContainerFactoryScheduleDate() {
        ConcurrentKafkaListenerContainerFactory<String, ScheduleDateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(ScheduleDateDTO.class))
        );

        factory.setReplyTemplate(new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs())));

        return factory;
    }

    @Bean
    public KafkaTemplate<String, SysadminResourceManagerView> kafkaTemplateSysadminView() {
        return new KafkaTemplate<>(producerFactorySysadminView());
    }

    @Bean
    public ProducerFactory<String, SysadminResourceManagerView> producerFactorySysadminView() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ConsumerFactory<String, DateDTO> consumerFactoryDateDTO() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(), new JsonDeserializer<>(DateDTO.class));
    }

    /**
     * This method will return the kafkaListener so that we can listen to messages with the @KafkaListener annotation.
     * @return the concurrentListenerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DateDTO>
        kafkaListenerContainerFactoryClusterDateDTO() {
        ConcurrentKafkaListenerContainerFactory<String, DateDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryDateDTO());

        //Setup of reply template
        factory.setReplyTemplate(kafkaTemplateString());

        return factory;
    }
}
