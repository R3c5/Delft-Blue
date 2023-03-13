//package nl.tudelft.sem.template.kafka.config;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//
//
//@Configuration
//public class KafkaConsumerConfig {
//
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String boostrapServers;
//
//    /**
//     * This method return the config of the consumer. It will specify which server does it consume from, what
//     * type of content does it consume as well as the deserializer for that content
//     *
//     * @return the Configuration
//     */
//    public Map<String, Object> consumerConfig() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        return props;
//    }
//
//    /**
//     * This method will create a consumerFactory.
//     *
//     * @return a ConsumerFactory
//     */
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfig());
//    }
//
//
//    /**
//     * This method will create a KafkaListenerFactory.
//     *
//     * @param consumerFactory a consumer factory
//     * @return KafkaListenerContainerFactory of concurrent message listeners
//     */
//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> factory(
//            ConsumerFactory<String, String> consumerFactory
//    ) {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }
//
//    public String getBoostrapServers() {
//        return boostrapServers;
//    }
//
//    public void setBoostrapServers(String boostrapServers) {
//        this.boostrapServers = boostrapServers;
//    }
//}
