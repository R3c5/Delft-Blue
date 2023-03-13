package sem.faculty.controllers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Controller;
import sem.commons.ScheduleDateDTO;
import sem.commons.StatusDTO;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
public class ScheduleRequestController {
    private final transient ReplyingKafkaTemplate<String, ScheduleDateDTO, LocalDate>
            replyingKafkaScheduleRequest;

    private final transient ReplyingKafkaTemplate<String, ScheduleDateDTO, StatusDTO>
            replyingKafkaReserveResources;

    @Autowired
    public ScheduleRequestController(ReplyingKafkaTemplate<String,
                                             ScheduleDateDTO, LocalDate> replyingKafkaScheduleRequest,
                                     ReplyingKafkaTemplate<String,
                                             ScheduleDateDTO, StatusDTO> replyingKafkaReserveResources) {
        this.replyingKafkaScheduleRequest = replyingKafkaScheduleRequest;
        this.replyingKafkaReserveResources = replyingKafkaReserveResources;
    }

    /**
     * Send scheduleDateDTO to topic schedule-resource.
     *
     * @param scheduleDateDTO - scheduleDateDTO sent to the topic `schedule-date`
     * @return ResponseEntity with a LocalDate of the date found to be available or ResponseEntity
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public ResponseEntity<LocalDate> sendScheduleRequest(ScheduleDateDTO scheduleDateDTO)
            throws ExecutionException, InterruptedException {
        ProducerRecord<String, ScheduleDateDTO> record =
                new ProducerRecord<>("schedule-date", scheduleDateDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "schedule-date-reply".getBytes()));

        RequestReplyFuture<String, ScheduleDateDTO, LocalDate> sendAndReceive =
                replyingKafkaScheduleRequest.sendAndReceive(record);

        ConsumerRecord<String, LocalDate> consumerRecord;
        try {
            consumerRecord = sendAndReceive.get(20, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(consumerRecord.value());
    }

    /**
     * Send a reserve message to `reserve-resources` topic.
     * @param scheduleDateDTO - ScheduleDateDTO for which the reserve message applies
     * @return a statusDTO with OK if reserve is succesfull and other message if not
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public StatusDTO sendReserveResources(ScheduleDateDTO scheduleDateDTO) {
        ProducerRecord<String, ScheduleDateDTO> record =
                new ProducerRecord<>("reserve-resources", scheduleDateDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "reserve-resources-reply".getBytes()));

        RequestReplyFuture<String, ScheduleDateDTO, StatusDTO> sendAndReceive =
                replyingKafkaReserveResources.sendAndReceive(record);

        ConsumerRecord<String, StatusDTO> consumerRecord;
        try {
            consumerRecord = sendAndReceive.get(20, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new StatusDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        return consumerRecord.value();
    }
}
