package sem.faculty.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import sem.commons.*;

import java.time.LocalDate;

/**
 * This class has all the configurations that allow for the consuming and replying of messages. There is some
 * duplication with the ProducerConfiguration and ConsumerConfiguration classes but this way it improves readability
 * and understanding of what is needed to consume and produce messages
 */
@Configuration
public class ReplyingTemplateConfiguration {

    /**
     * This method will set up the kafkaTemplate for Consuming and Replying to messages.
     *
     * @param pf        the producerFactory to reply
     * @param container the listener container
     * @return the kafkaTemplate
     */
    @Bean
    public ReplyingKafkaTemplate<String, ScheduleDateDTO, LocalDate> replyKafkaTemplateScheduleDateDTO(
            ProducerFactory<String, ScheduleDateDTO> pf,
            KafkaMessageListenerContainer<String, LocalDate> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    /**
     * This method will return the container used in replyKafkaTemplatePendingRequests.
     *
     * @param cf the consumer factory
     * @return the listenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, LocalDate> replyContainerScheduleDateDTO(
            ConsumerFactory<String, LocalDate> cf) {
        ContainerProperties containerProperties = new ContainerProperties("schedule-date-reply");
        containerProperties.setGroupId("default");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    /**
     * This method will set up the kafkaTemplate for Consuming and Replying to messages.
     *
     * @param pf        the producerFactory to reply
     * @param container the listener container
     * @return the kafkaTemplate
     */
    @Bean
    public ReplyingKafkaTemplate<String, ScheduleDateDTO, StatusDTO> replyKafkaTemplateReserveResources(
            ProducerFactory<String, ScheduleDateDTO> pf,
            KafkaMessageListenerContainer<String, StatusDTO> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    /**
     * This method will return the container used in replyKafkaTemplatePendingRequests.
     *
     * @param cf the consumer factory
     * @return the listenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, StatusDTO> replyContainerReserveResources(
            ConsumerFactory<String, StatusDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("reserve-resources-reply");
        containerProperties.setGroupId("default");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }
}

