package nl.tudelft.sem.notification.manager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic publishNotificationsTopic() {
        return TopicBuilder.name("publish-notification")
                .build();
    }

    @Bean
    public NewTopic pollNotificationsTopic() {
        return TopicBuilder.name("poll-notifications")
                .build();
    }
}
