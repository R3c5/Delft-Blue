package nl.tudelft.sem.notification.manager.controllers;

import nl.tudelft.sem.notification.manager.domain.notification.Notification;
import nl.tudelft.sem.notification.manager.domain.notification.NotificationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import sem.commons.NetIdDTO;
import sem.commons.NotificationDTO;
import sem.commons.NotificationPackage;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NotificationController {

    private final transient NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * This method will other microservices to publish notifications for a certain user. They will be stored for later
     * retrieval
     * @param record the consumerRecord
     * @param payload the NotificationDTO representing the notification to publish
     */
    @KafkaListener(topics = "publish-notification",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryNotification"
    )
    public void saveNotification(ConsumerRecord<String, NotificationDTO> record, @Payload NotificationDTO payload) {
        System.out.println(payload.toString());
        Notification toPersist = new Notification(payload.getOwnerNetId(), payload.getDescription());
        notificationRepository.saveAndFlush(toPersist);
    }

    /**
     * This method will allow users to poll their pending/new notifications.
     * @param record the consumerRecord
     * @param netId the netID from the user to retrieve the new notifications from
     * @return A package with their new Notifications
     */
    @KafkaListener(
            topics = "poll-notifications",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryNetId"
    )
    @SendTo
    @Transactional
    public NotificationPackage getNotifications(ConsumerRecord<String, NetIdDTO> record, @Payload NetIdDTO netId) {
        // notificationRepository.saveAndFlush(new Notification(netId.getNetId(), "test"));
        String ownerNetId = netId.getNetId();
        List<Notification> notifications = notificationRepository.findByOwnerNetIdAndSeen(ownerNetId, false);
        notifications.forEach(x -> notificationRepository.updateNotificationSeenById(x.getId()));
        return new NotificationPackage(notifications.stream().map(x ->
                        new NotificationDTO(x.getOwnerNetId(), x.getDescription())).collect(Collectors.toList()));
    }

}
