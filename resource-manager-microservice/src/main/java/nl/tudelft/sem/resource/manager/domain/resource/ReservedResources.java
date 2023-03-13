package nl.tudelft.sem.resource.manager.domain.resource;

import lombok.*;
import nl.tudelft.sem.resource.manager.domain.Resource;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DDD entity representing the number of resources allocated to a Faculty on a given Date.
 */

@Entity
@Table(name = "reserved_resources")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReservedResources {
    @EmbeddedId
    private ReservedResourceId id;

    @Embedded
    private Resource resources;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservedResources)) {
            return false;
        }
        ReservedResources that = (ReservedResources) o;
        return Objects.equals(id, that.id) && Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, resources);
    }
}
