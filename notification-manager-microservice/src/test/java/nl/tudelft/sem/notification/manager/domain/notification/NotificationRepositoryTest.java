package nl.tudelft.sem.notification.manager.domain.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        notification1 = new Notification("testId1", "testDescription2");
        notification2 = new Notification("testId2", "testDescription2");
        notification3 = new Notification("testId1", "testDescription3");
        notification3.setSeen(true);
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
    }

    @Test
    void findByOwnerNetIdAndSeenTest() {
        List<Notification> toCheck = List.of(notification1);
        List<Notification> returned = notificationRepository.findByOwnerNetIdAndSeen("testId1", false);
        assertThat(returned).isEqualTo(toCheck);
    }

    @Test
    void updateNotificationSeenByIdTest() {
        notificationRepository.updateNotificationSeenById(notification1.getId());
        List<Notification> toCheck = new ArrayList<>();
        List<Notification> returned = notificationRepository.findByOwnerNetIdAndSeen("testId1", false);
        assertThat(returned).isEqualTo(toCheck);
    }



}