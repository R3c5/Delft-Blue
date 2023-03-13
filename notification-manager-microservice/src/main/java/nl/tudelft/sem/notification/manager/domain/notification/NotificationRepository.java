package nl.tudelft.sem.notification.manager.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT s FROM Notification s WHERE s.ownerNetId = ?1 AND s.seen = ?2")
    List<Notification> findByOwnerNetIdAndSeen(String netId, boolean seen);

    @Modifying
    @Query("UPDATE Notification SET seen = true WHERE id = ?1")
    void updateNotificationSeenById(long id);
}
