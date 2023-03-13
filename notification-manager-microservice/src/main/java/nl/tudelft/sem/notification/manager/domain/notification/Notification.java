package nl.tudelft.sem.notification.manager.domain.notification;

import lombok.*;

import javax.persistence.*;

/**
 * A DDD entity representing user Notification.
 */
@Entity
@Table(name = "Notification")
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Notification {

    /**
     * The identifier for the Notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "ownerNetId", nullable = false, unique = false)
    @Convert(converter = StringAttributeConverter.class)
    private String ownerNetId;

    @Column(name = "description", nullable = false, unique = false)
    @Convert(converter = StringAttributeConverter.class)
    private String description;

    @Column(name = "seen", nullable = false, updatable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean seen;

    /**
     * Constructor for the Notification object.
     * @param ownerNetId the netId of the respective owner
     * @param description the description of the notification
     */
    public Notification(String ownerNetId, String description) {
        this.ownerNetId = ownerNetId;
        this.description = description;
        this.seen = false;
    }
}
