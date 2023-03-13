package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.AcceptRequestsDTO;
import sem.commons.RequestDTO;
import sem.commons.StatusDTO;

import java.util.concurrent.ExecutionException;

@RestController
public class UserRequestController {

    private transient AuthManager authManager;

    private transient ReplyingKafkaTemplate<String, RequestDTO, StatusDTO> replyKafkaTemplateRequestStatus;

    @Autowired
    public UserRequestController(
            AuthManager authManager,
            ReplyingKafkaTemplate<String, RequestDTO, StatusDTO> replyingKafkaTemplate
    ) {
        this.authManager = authManager;
        this.replyKafkaTemplateRequestStatus = replyingKafkaTemplate;
    }

    /**
     * Sends a request trough kafka to the "incoming-request" topic.
     * Catches JsonProcessingException - when the requestDTO cannot be converted to json
     *
     * @param requestDTO - the DTO to be sent
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @PostMapping("request/new")
    public ResponseEntity<StatusDTO> sendUserRequest(@RequestBody RequestDTO requestDTO) {
        if (!authManager.getFaculties().contains(requestDTO.getRequestFacultyInformation().getFaculty().toString())) {
            return ResponseEntity.status(401).body(
                    new StatusDTO("You can only make requests to your own faculty!"));
        }
        if (!authManager.getNetId().equals(requestDTO.getRequestFacultyInformation().getNetId())) {
            return ResponseEntity.status(401).body(
                    new StatusDTO("You cannot make requests for someone else!"));
        }
        ProducerRecord<String, RequestDTO> record =
                new ProducerRecord<>("incoming-request", requestDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "incoming-request-reply".getBytes()));

        RequestReplyFuture<String, RequestDTO, StatusDTO> sendAndReceive =
                replyKafkaTemplateRequestStatus.sendAndReceive(record);

        ConsumerRecord<String, StatusDTO> consumerRecord;

        try {
            consumerRecord = sendAndReceive.get();
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        StatusDTO statusDTO = consumerRecord.value();

        if (statusDTO.getStatus().equals("OK")) {
            return ResponseEntity.ok(new StatusDTO("Request was sent"));
        } else {
            return ResponseEntity.status(400).body(statusDTO);
        }
    }
}