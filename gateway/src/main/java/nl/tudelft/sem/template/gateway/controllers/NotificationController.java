package nl.tudelft.sem.template.gateway.controllers;

import lombok.Getter;
import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.coyote.Response;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping("notification/")
@Getter
public class NotificationController {

    private ReplyingKafkaTemplate<String, NetIdDTO, NotificationPackage>
            replyingKafkaTemplatePendingNotifications;

    private AuthManager authManager;



    @Autowired
    public NotificationController(ReplyingKafkaTemplate<String, NetIdDTO, NotificationPackage>
                                              replyingKafkaTemplatePendingNotifications,
                                  AuthManager authManager) {
        this.replyingKafkaTemplatePendingNotifications = replyingKafkaTemplatePendingNotifications;
        this.authManager = authManager;
    }

    /**
     * This method will accept requests for new notifications for a certain user.
     * @param netId of the user to retrieve the notifications. There will be checks for identity
     * @return the Notification Package (list of new notifications)
     * @throws ExecutionException in case of Timeout in the retrieval
     * @throws InterruptedException in case of Timeout in the retrieval
     * @throws TimeoutException in case of Timeout in the retrieval
     */
    @GetMapping("{netId}")
    public ResponseEntity<NotificationPackage> getNewNotifications(@PathVariable("netId") String netId) throws
            ExecutionException, InterruptedException, TimeoutException {
        if (!checkIdentity(netId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ProducerRecord<String, NetIdDTO> record =
                new ProducerRecord<>("poll-notifications", new NetIdDTO(netId));
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "poll-notifications-reply".getBytes()));

        RequestReplyFuture<String, NetIdDTO, NotificationPackage> sendAndReceive =
                replyingKafkaTemplatePendingNotifications.sendAndReceive(record);

        ConsumerRecord<String, NotificationPackage> consumerRecord = sendAndReceive.get(10, TimeUnit.SECONDS);

        return ResponseEntity.ok(consumerRecord.value());
    }

    /**
     * To check the true identity of this user. This will prevent people from retrieving someone else's notifications
     * @param netId of the user to check
     * @return the boolean representing if the check was passed or no
     */
    public boolean checkIdentity(String netId) {
        return netId.equals(authManager.getNetId());
    }
}
