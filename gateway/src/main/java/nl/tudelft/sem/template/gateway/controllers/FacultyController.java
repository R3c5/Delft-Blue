package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.AcceptRequestsDTO;
import sem.commons.FacultyNameDTO;
import sem.commons.PendingRequestsDTO;
import sem.commons.StatusDTO;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Faculty controller.
 */
@RestController
public class FacultyController {

    private transient AuthManager authManager;

    private transient ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> replyingKafkaTemplatePendingRequests;

    private transient ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> replyingKafkaTemplateAcceptRequests;

    /**
     * Instantiates a new Faculty controller.
     *
     * @param replyingKafkaTemplatePendingRequests the replying kafka template pending requests
     * @param replyingKafkaTemplateAcceptRequests  the replying kafka template accept requests
     */
    @Autowired
    public FacultyController(
            AuthManager authManager,
            ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> replyingKafkaTemplatePendingRequests,
            ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> replyingKafkaTemplateAcceptRequests) {
        this.authManager = authManager;
        this.replyingKafkaTemplatePendingRequests = replyingKafkaTemplatePendingRequests;
        this.replyingKafkaTemplateAcceptRequests = replyingKafkaTemplateAcceptRequests;
    }


    /**
     * Gets pending requests.
     *
     * @param facultyNameDTO the faculty name dto
     * @return the pending requests
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     * @throws TimeoutException     the timeout exception
     */
    @PostMapping("/faculty/pending")
    public ResponseEntity<PendingRequestsDTO> getPendingRequests(
            @RequestBody FacultyNameDTO facultyNameDTO) throws ExecutionException, InterruptedException, TimeoutException {

        if (!authManager.getFaculties().contains(facultyNameDTO.getFacultyName())) {
            return ResponseEntity.status(401).body(
                    new PendingRequestsDTO("You are not allowed to access this faculty", new ArrayList<>()));
        }
        // create producer record
        ProducerRecord<String, FacultyNameDTO> record =
                new ProducerRecord<String, FacultyNameDTO>("pendingRequestsTopic", facultyNameDTO);
        // set reply topic in header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "pendingRequestsTopicReply".getBytes()));
        // post in kafka topic
        RequestReplyFuture<String, FacultyNameDTO, PendingRequestsDTO> sendAndReceive =
                replyingKafkaTemplatePendingRequests.sendAndReceive(record);

        // get consumer record
        ConsumerRecord<String, PendingRequestsDTO> consumerRecord = sendAndReceive.get(10, TimeUnit.SECONDS);
        // return consumer value

        PendingRequestsDTO pendingRequestsDTO = consumerRecord.value();

        if (pendingRequestsDTO.getStatus().equals("OK")) {
            return ResponseEntity.ok(pendingRequestsDTO);
        } else {
            return ResponseEntity.status(400).body(pendingRequestsDTO);
        }
    }

    /**
     * Accept requests response entity.
     *
     * @param acceptRequestsDTO the accept requests dto
     * @return the response entity
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    @PostMapping("/faculty/accept")
    public ResponseEntity<StatusDTO> acceptRequests(@RequestBody AcceptRequestsDTO acceptRequestsDTO)
            throws ExecutionException, InterruptedException {
        if (!authManager.getFaculties().contains(acceptRequestsDTO.getFacultyName())) {
            return ResponseEntity.status(401).body(
                    new StatusDTO("You are not allowed to access this faculty"));
        }
        ProducerRecord<String, AcceptRequestsDTO> record =
                new ProducerRecord<>("acceptRequestsTopic", acceptRequestsDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "acceptRequestsTopicReply".getBytes()));

        RequestReplyFuture<String, AcceptRequestsDTO, StatusDTO> sendAndReceive =
                replyingKafkaTemplateAcceptRequests.sendAndReceive(record);

        ConsumerRecord<String, StatusDTO> consumerRecord = sendAndReceive.get();

        StatusDTO statusDTO = consumerRecord.value();

        if (statusDTO.getStatus().equals("OK")) {
            return ResponseEntity.ok(statusDTO);
        } else {
            return ResponseEntity.status(400).body(statusDTO);
        }
    }
}
