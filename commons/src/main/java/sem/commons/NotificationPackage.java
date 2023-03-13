package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * A DTO for a Notification package, a collection of Notifications for the same user.
 */
@Data
public class NotificationPackage {

    private List<NotificationDTO> notifications;

    public NotificationPackage(
            @JsonProperty("notifications") List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

}
