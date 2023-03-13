package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A DTO representing the name of the Node.
 */

@Data
public class OwnerName {
    public final transient String name;

    public OwnerName(
            @JsonProperty("name") String name
    ) {
        this.name = name;
    }
}
