package nl.tudelft.sem.resource.manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration for the topics used by the microservice.
 * These are primarily used to communicate with faculty-microservice or user-microservice
 */

@Configuration
public class KafkaTopicConfig {

    /**
     * Instantiates a new Topic.
     * This topic should be used for communication with the faculty-microservice to
     * establish on what date the Request should be serviced.
     *
     * @return a new Topic
     */
    @Bean
    public NewTopic fitRequestTopic() {
        return TopicBuilder.name("fit-request")
                .build();
    }

    /**
     * Instantiates a new Topic.
     * This topic should be used for communication with the gateway to get the regular userView
     * of the system.
     * @return a new Topic
     */
    @Bean
    public NewTopic getUserView() {
        return TopicBuilder.name("user-view")
                .build();
    }

    /**
     * Instantiates a new Topic.
     * This topic should be used for communication with the gateway to add a new ClusterNode to the system
     * @return a new Topic
     */
    @Bean
    public NewTopic getAddNode() {
        return TopicBuilder.name("add-node")
                .build();
    }


    @Bean
    public NewTopic getRemoveNode() {
        return TopicBuilder.name("remove-node")
                .build();
    }

    @Bean
    public NewTopic getScheduleDate() {
        return TopicBuilder.name("schedule-date")
                .build();
    }

    @Bean
    public NewTopic getReserveResources() {
        return TopicBuilder.name("reserve-resources")
                .build();
    }
}
