package nl.tudelft.sem.notification.manager.controllers;

import nl.tudelft.sem.notification.manager.domain.notification.Notification;
import nl.tudelft.sem.notification.manager.domain.notification.NotificationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import sem.commons.NetIdDTO;
import sem.commons.NotificationDTO;
import sem.commons.NotificationPackage;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class NotificationControllerTest {


    private NotificationController consumer;
    private NotificationRepository notificationRepository;
    private String topic = "publish-notification";

    @BeforeEach
    void setUp() {
        notificationRepository = Mockito.mock(NotificationRepository.class);
        consumer = new NotificationController(notificationRepository);
    }

    @Test
    public void testReceivingNotification() {
        NotificationDTO testNotification = new NotificationDTO("testId", "testDesc");
        consumer.saveNotification(new ConsumerRecord<>(topic, 1, 0, "sd", testNotification), testNotification);
        verify(notificationRepository).saveAndFlush(
                new Notification(testNotification.getOwnerNetId(), testNotification.getDescription()));
    }

    @Test
    public void testRetrievingNotifications() {
        Notification notification1 = new Notification("testId", "description1");
        Notification notification2 = new Notification("testId", "description2");
        List<Notification> listToReturn = List.of(notification1, notification2);
        NotificationPackage notificationPackage = new NotificationPackage(listToReturn.stream().map(x ->
                        new NotificationDTO(x.getOwnerNetId(), x.getDescription())).collect(Collectors.toList()));
        when(notificationRepository.findByOwnerNetIdAndSeen("testId", false)).thenReturn(listToReturn);
        NotificationPackage returned = consumer.getNotifications(
                new ConsumerRecord<>("test", 1, 1L, "test", new NetIdDTO("testId")), new NetIdDTO("testId"));
        assertThat(returned).isEqualTo(notificationPackage);
    }
}