package nl.tudelft.sem.template.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import sem.commons.*;

@Configuration
public class ReplyingTemplateFacultyConfiguration {

    /**
     * This method will set up the kafkaTemplate for Consuming and Replying to messages.
     * @param pf the producerFactory to reply
     * @param container the listener container
     * @return the kafkaTemplate
     */
    @Bean
    public ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> replyKafkaTemplatePendingRequests(
            ProducerFactory<String, FacultyNameDTO> pf,
            KafkaMessageListenerContainer<String, PendingRequestsDTO> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    /**
     * This method will return the container used in replyKafkaTemplatePendingRequests.
     * @param cf the consumer factory
     * @return the listenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, PendingRequestsDTO> replyContainerPendingRequests(
            ConsumerFactory<String, PendingRequestsDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("pendingRequestsTopicReply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> replyKafkaTemplateAcceptRequests(
            ProducerFactory<String, AcceptRequestsDTO> pf,
            @Qualifier("replyContainerAcceptRequests") KafkaMessageListenerContainer<String, StatusDTO> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, StatusDTO> replyContainerAcceptRequests(
            ConsumerFactory<String, StatusDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("acceptRequestsTopicReply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, DateDTO, SysadminScheduleDTO> replyKafkaTemplateSysadminResourceView(
            ProducerFactory<String, DateDTO> pf,
            KafkaMessageListenerContainer<String, SysadminScheduleDTO> container)  {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, SysadminScheduleDTO> replyContainerSysadminScheduleView(
            ConsumerFactory<String, SysadminScheduleDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("sysadmin-view-faculty-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, RequestDTO, StatusDTO> replyKafkaTemplateRequestStatus(
            ProducerFactory<String, RequestDTO> pf,
            @Qualifier("replyContainerRequestStatus") KafkaMessageListenerContainer<String, StatusDTO> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, StatusDTO> replyContainerRequestStatus(
            ConsumerFactory<String, StatusDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("incoming-request-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


}
