//package nl.tudelft.sem.template.authentication.kafkaconfiguration;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import sem.commons.ExampleUser;
//
///**
// * The type Producer controller.
// */
//@Service
//
//public class ProducerController {
//
//    private final transient KafkaTemplate<String, Object> template;
//
//    private final transient String topic;
//
//    /**
//     * Instantiates a new Producer controller.
//     *
//     * @param template the template
//     * @param topic    the topic
//     */
//    public ProducerController(KafkaTemplate<String, Object> template, @Value(value = "${kafka.topic}") String topic) {
//        this.template = template;
//        this.topic = topic;
//    }
//
//
//    /**
//     * Produce.
//     *
//     * @param netId the net id
//     */
//    @Async
//    public void produce(String netId) {
//        template.send(topic, netId, new ExampleUser(netId));
//        System.out.println("Message sent");
//    }
//}