package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import sem.commons.NetIdDTO;
import sem.commons.NotificationDTO;
import sem.commons.NotificationPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class NotificationControllerTest {

    @Mock
    private ReplyingKafkaTemplate<String, NetIdDTO, NotificationPackage> replyingKafkaTemplatePendingNotificationsMock;

    @Mock
    private AuthManager authManagerMock;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        replyingKafkaTemplatePendingNotificationsMock = Mockito.mock(ReplyingKafkaTemplate.class);
        authManagerMock = Mockito.mock(AuthManager.class);
        notificationController = new NotificationController(replyingKafkaTemplatePendingNotificationsMock,
                authManagerMock);
    }

    /**
     * Tests that the interaction with the kafkaTemplate is the correct one.
     * @throws ExecutionException if timeout in retrieval
     * @throws InterruptedException if timeout in retrieval
     * @throws TimeoutException if timeout in retrieval
     */
    @Test
    void kafkaTemplateInteractionTest() throws ExecutionException, InterruptedException, TimeoutException {
        NotificationDTO testNotification = new NotificationDTO("test", "successful");
        RequestReplyFuture<String, NetIdDTO, NotificationPackage> future = Mockito.mock(RequestReplyFuture.class);
        ConsumerRecord<String, NotificationPackage> consumerRecord =
                new ConsumerRecord<>("test", 1, 1L, "test", new NotificationPackage(List.of(testNotification)));
        when(future.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        when(replyingKafkaTemplatePendingNotificationsMock.sendAndReceive(any())).thenReturn(future);
        when(authManagerMock.getNetId()).thenReturn("test");

        ResponseEntity<NotificationPackage> returned = notificationController.getNewNotifications("test");
        assertThat(returned.getBody()).isEqualTo(new NotificationPackage(List.of(testNotification)));
    }

    @Test
    void testDeniedWhenIdentityDifferent() throws ExecutionException, InterruptedException, TimeoutException {
        when(authManagerMock.getNetId()).thenReturn("notTheSame");
        ResponseEntity<NotificationPackage> returnedPackage = notificationController.getNewNotifications("test");
        NotificationPackage emptyPackage = new NotificationPackage(new ArrayList<>());
        assertThat(returnedPackage.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void checkIdentityTest() {
        when(authManagerMock.getNetId()).thenReturn("notTheSame");
        boolean returned = notificationController.checkIdentity("test");
        assertThat(returned).isFalse();
    }
}