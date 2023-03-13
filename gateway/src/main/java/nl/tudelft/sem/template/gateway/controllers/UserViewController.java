package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import sem.commons.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("userView/")
public class UserViewController {

    private final transient ReplyingKafkaTemplate<String, FacultyNamePackageDTO, RegularUserView>
            replyingKafkaTemplateUserView;

    private final transient ReplyingKafkaTemplate<String, DateDTO, SysadminResourceManagerView>
            replyingKafkaTemplateSysadminResourceView;

    private final transient ReplyingKafkaTemplate<String, DateDTO, SysadminScheduleDTO>
            replyingKafkaTemplateSysadminScheduleDTO;

    private final transient AuthManager authManager;

    /**
     * This is the constructor of the controller.
     * @param replyingKafkaTemplateUserView the kafka template for the regular user view
     * @param replyingKafkaTemplateSysadminResourceView the kafka template for the sysadmin resource view
     * @param replyingKafkaTemplateSysadminScheduleDTO the kafka template for the sysadmin schedule view
     * @param authManager the auth manager
     */
    @Autowired
    public UserViewController(
            ReplyingKafkaTemplate<String, FacultyNamePackageDTO, RegularUserView> replyingKafkaTemplateUserView,
            ReplyingKafkaTemplate<String, DateDTO, SysadminResourceManagerView> replyingKafkaTemplateSysadminResourceView,
            ReplyingKafkaTemplate<String, DateDTO, SysadminScheduleDTO> replyingKafkaTemplateSysadminScheduleDTO,
            AuthManager authManager) {
        this.replyingKafkaTemplateUserView = replyingKafkaTemplateUserView;
        this.replyingKafkaTemplateSysadminResourceView = replyingKafkaTemplateSysadminResourceView;
        this.replyingKafkaTemplateSysadminScheduleDTO = replyingKafkaTemplateSysadminScheduleDTO;
        this.authManager = authManager;
    }

    /**
     * This is the endpoint that users must target in order to see the available resources for the following
     * day for the faculties they belong to.
     * @return a class that basically maps faculty names (their faculties) to the amount of available resources
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @GetMapping("/regularUser")
    public ResponseEntity<RegularUserView> getNormalUserView() {
        ProducerRecord<String, FacultyNamePackageDTO> record =
                new ProducerRecord<>("user-view", new FacultyNamePackageDTO(authManager.getFaculties()));

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "user-view-reply".getBytes()));

        RequestReplyFuture<String, FacultyNamePackageDTO, RegularUserView> sendAndReceive =
                replyingKafkaTemplateUserView.sendAndReceive(record);

        ConsumerRecord<String, RegularUserView> consumerRecord;
        try {
            consumerRecord = sendAndReceive.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(consumerRecord.value());
    }

    /**
     * The endpoint for the sysadmin view of the system.
     * @param dateDTO the date to have the view of
     * @return the sysadmin view
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @PostMapping("/sysadmin")
    public ResponseEntity<SysadminUserView> getSysadminView(@RequestBody DateDTO dateDTO) {
        ProducerRecord<String, DateDTO> recordResource =
                new ProducerRecord<>("sysadmin-view", dateDTO);

        recordResource.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "sysadmin-view-reply".getBytes()));
        RequestReplyFuture<String, DateDTO, SysadminResourceManagerView> sendAndReceiveResource =
                replyingKafkaTemplateSysadminResourceView.sendAndReceive(recordResource);

        ConsumerRecord<String, SysadminResourceManagerView> consumerRecordResource;

        try {
            consumerRecordResource = sendAndReceiveResource.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ProducerRecord<String, DateDTO> recordFacutly =
                new ProducerRecord<>("sysadmin-view-faculty", dateDTO);

        recordFacutly.headers()
                .add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "sysadmin-view-faculty-reply".getBytes()));
        RequestReplyFuture<String, DateDTO, SysadminScheduleDTO> sendAndReceiveFaculty =
                replyingKafkaTemplateSysadminScheduleDTO.sendAndReceive(recordFacutly);

        ConsumerRecord<String, SysadminScheduleDTO> consumerRecordFaculty;

        try {
            consumerRecordFaculty = sendAndReceiveFaculty.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(new SysadminUserView(consumerRecordResource.value(), consumerRecordFaculty.value()));
    }
}
