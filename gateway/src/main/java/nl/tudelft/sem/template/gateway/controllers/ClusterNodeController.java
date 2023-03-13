package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.ClusterNodeDTO;
import sem.commons.Token;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("clusterNode/")
public class ClusterNodeController {

    private final transient ReplyingKafkaTemplate<String, ClusterNodeDTO, String> kafkaTemplateClusterNodeDTO;

    private final transient ReplyingKafkaTemplate<String, Token, String> kafkaTemplateToken;
    private final transient AuthManager authManager;

    /**
     * The constructor for the controller.
     * @param kafkaTemplateClusterNodeDTO the kafka template for the ClusterNodeDTO
     * @param kafkaTemplateToken the kafka template for the Token
     * @param authManager the authentication manager
     */
    @Autowired
    public ClusterNodeController(
                                 ReplyingKafkaTemplate<String, ClusterNodeDTO, String> kafkaTemplateClusterNodeDTO,
                                 ReplyingKafkaTemplate<String, Token, String> kafkaTemplateToken,
                                 AuthManager authManager) {
        this.kafkaTemplateClusterNodeDTO = kafkaTemplateClusterNodeDTO;
        this.authManager = authManager;
        this.kafkaTemplateToken = kafkaTemplateToken;
    }

    /**
     * This method is the api endpoint to add cluster nodes to the system.
     * @param clusterNodeDTO the DTO representing the cluster node to add
     * @return a ResponseEntity specifying weather or not the node was added successfully
     */
    @PostMapping("/add")
    public ResponseEntity<String> addClusterNode(@RequestBody ClusterNodeDTO clusterNodeDTO)
            throws ExecutionException, InterruptedException, TimeoutException {
        if (!clusterNodeDTO.getOwnerName().getName().equals(authManager.getNetId())) {
            return ResponseEntity.status(401).body("You can only submit new nodes in your own name!");
        }

        ProducerRecord<String, ClusterNodeDTO> record = new ProducerRecord<>("add-node", clusterNodeDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "add-node-reply".getBytes()));

        RequestReplyFuture<String, ClusterNodeDTO, String> sendAndReceive =
                kafkaTemplateClusterNodeDTO.sendAndReceive(record);

        ConsumerRecord<String, String> consumerRecord = sendAndReceive.get(10, TimeUnit.SECONDS);

        return ResponseEntity.ok(consumerRecord.value());
    }

    /**
     * This method is the api endpoint to remove cluster nodes from the system.
     * @param token the token that identifies the token to remove
     * @return success/failure message
     * @throws ExecutionException in case the reply times out
     * @throws InterruptedException in case the reply times out
     * @throws TimeoutException in case the reply times out
     */
    @PostMapping("/remove")
    public ResponseEntity<String> removeClusterNode(@RequestBody Token token)
            throws ExecutionException, InterruptedException, TimeoutException {
        ProducerRecord<String, Token> record = new ProducerRecord<>("remove-node", token);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "remove-node-reply".getBytes()));
        RequestReplyFuture<String, Token, String> sendAndReceive =
                kafkaTemplateToken.sendAndReceive(record);
        ConsumerRecord<String, String> consumerRecord = sendAndReceive.get(10, TimeUnit.SECONDS);

        return ResponseEntity.ok(consumerRecord.value());
    }

}
