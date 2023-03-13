package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * A DTO for the Notification object.
 */
@Data
public class NotificationDTO {

    /**
     * The identifier for the Notification.
     */

    private String ownerNetId;

    private String description;

    /**
     * Constructor for the Notification DTO.
     * @param ownerNetId the netId of the respective owner
     * @param description the description of the notification
     */
    public NotificationDTO(
            @JsonProperty("ownerNetId") String ownerNetId,
            @JsonProperty("description") String description) {
        this.ownerNetId = ownerNetId;
        this.description = description;
    }
}
