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

/**
 * This class has all the configurations that allow for the consuming and replying of messages. There is some
 * duplication with the ProducerConfiguration and ConsumerConfiguration classes but this way it improves readability
 * and understanding of what is needed to consume and produce messages
 */
@Configuration
public class ReplyingTemplateConfiguration {



    /**
     * This method will set up the kafkaTemplate for Consuming and Replying to messages.
     * @param pf the producerFactory to reply
     * @param container the listener container
     * @return the kafkaTemplate
     */
    @Bean
    public ReplyingKafkaTemplate<String, NetIdDTO, NotificationPackage> replyKafkaTemplatePendingNotifications(
            ProducerFactory<String, NetIdDTO> pf,
            KafkaMessageListenerContainer<String, NotificationPackage> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    /**
     * This method will return the container used in replyKafkaTemplatePendingNotifications.
     * @param cf the consumer factory
     * @return the listenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, NotificationPackage> replyContainerPendingNotifications(
            ConsumerFactory<String, NotificationPackage> cf) {
        ContainerProperties containerProperties = new ContainerProperties("poll-notifications-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    /**
     * Reply kafka template user replying kafka template.
     *
     * @param pf the pf
     * @param cf the cf
     * @return the replying kafka template
     */
    @Bean
    public ReplyingKafkaTemplate<String, UserDTO, StatusDTO> replyKafkaTemplateUser(
            ProducerFactory<String, UserDTO> pf,
            ConsumerFactory<String, StatusDTO> cf
    ) {
        //SPECIAL TEMPLATE AS LISTENER FOR StatusDTO IS REUSED WITH DIFFERENT TOPIC, DO NOT COPY MINDLESSLY
        ContainerProperties containerProperties = new ContainerProperties("add-user-topic-reply");
        KafkaMessageListenerContainer<String, StatusDTO> container =
                new KafkaMessageListenerContainer<>(cf, containerProperties);
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> replyKafkaTemplateTokenDTO(
            ProducerFactory<String, UserCredentials> pf,
            KafkaMessageListenerContainer<String, TokenDTO> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, TokenDTO> replyContainerToken(
            ConsumerFactory<String, TokenDTO> cf) {
        ContainerProperties containerProperties = new ContainerProperties("user-auth-topic-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    /**
     * This method will set up the kafkaTemplate for Consuming and Replying to messages.
     * @param pf the producerFactory to reply
     * @param container the listener container
     * @return the kafkaTemplate
     */
    @Bean
    public ReplyingKafkaTemplate<String, FacultyNamePackageDTO, RegularUserView>
        replyKafkaTemplateUserView(
            ProducerFactory<String, FacultyNamePackageDTO> pf,
            KafkaMessageListenerContainer<String, RegularUserView> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    /**
     * This method will return the container used in replyKafkaTemplatePendingRequests.
     * @param cf the consumer factory
     * @return the listenerContainer
     */
    @Bean
    public KafkaMessageListenerContainer<String, RegularUserView> replyContainerUserView(
            ConsumerFactory<String, RegularUserView> cf) {
        ContainerProperties containerProperties = new ContainerProperties("user-view-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, DateDTO, SysadminResourceManagerView> replyKafkaTemplateSysadminView(
            ProducerFactory<String, DateDTO> pf,
            KafkaMessageListenerContainer<String, SysadminResourceManagerView> container)  {
        return new ReplyingKafkaTemplate<>(pf, container);
    }



    @Bean
    public ReplyingKafkaTemplate<String, ClusterNodeDTO, String> replyKafkaTemplateClusterNodeDTOString(
            ProducerFactory<String, ClusterNodeDTO> pf,
            @Qualifier("replyContainerClusterNodeDTOString") KafkaMessageListenerContainer<String, String> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, SysadminResourceManagerView> replyContainerSysadminResourceView(
            ConsumerFactory<String, SysadminResourceManagerView> cf) {
        ContainerProperties containerProperties = new ContainerProperties("sysadmin-view-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    @Bean
    public KafkaMessageListenerContainer<String, String> replyContainerClusterNodeDTOString(
            ConsumerFactory<String, String> cf) {
        ContainerProperties containerProperties = new ContainerProperties("add-node-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    @Bean
    public ReplyingKafkaTemplate<String, Token, String> replyKafkaTemplateTokenString(
            ProducerFactory<String, Token> pf,
            @Qualifier("replyContainerTokenString") KafkaMessageListenerContainer<String, String> container
    ) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> replyContainerTokenString(
            ConsumerFactory<String, String> cf) {
        ContainerProperties containerProperties = new ContainerProperties("remove-node-reply");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


}

