//package nl.tudelft.sem.template.authentication.kafkaconfiguration;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//import org.springframework.scheduling.annotation.EnableAsync;
//
//
///**
// * The type Producer configuration.
// */
//@EnableAsync
//@Configuration
//public class ProducerConfiguration {
//
//    private final transient KafkaProperties kafkaProperties;
//
//    private final transient String topic;
//
//    public ProducerConfiguration(KafkaProperties kafkaProperties, @Value(value = "${kafka.topic}") String topic) {
//        this.kafkaProperties = kafkaProperties;
//        this.topic = topic;
//    }
//
//    /**
//     * Producer configs map.
//     *
//     * @return the map
//     */
//    //@Bean
//    //public Map<String, Object> producerConfigs() {
//    //    Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
//    //    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//    //    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//    //    return props;
//    //}
//
//    /**
//     * Producer factory.
//     *
//     * @return the producer factory
//     */
//    //@Bean
//    //public ProducerFactory<String, Object> producerFactory() {
//    //    return new DefaultKafkaProducerFactory<>(producerConfigs());
//    //}
//
//    /**
//     * Kafka template kafka template.
//     *
//     * @return the kafka template
//     */
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    /**
//     * Advice topic new topic.
//     *
//     * @return the new topic
//     */
//    @Bean
//    public NewTopic adviceTopic() {
//        return new NewTopic(topic, 3, (short) 1);
//    }
//}