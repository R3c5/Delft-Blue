package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A DDD value object representing a NetID in our domain.
 */
@Data
public class NetIdDTO {
    private final transient String netId;

    public NetIdDTO(
            @JsonProperty("netId") String netId
    ) {
        // validate NetID
        this.netId = netId;
    }
}
