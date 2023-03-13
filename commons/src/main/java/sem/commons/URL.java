package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A DTO representing the URL at which the node is located.
 */

@Data
public class URL {
    public final transient String urlValue;

    public URL(
            @JsonProperty("urlValue") String urlValue
    ) {
        this.urlValue = urlValue;
    }
}
