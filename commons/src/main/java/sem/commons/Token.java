package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * A DTO representing the Token required to access the Node.
 */

@Data
public class Token {
    @EqualsAndHashCode.Include public final transient String tokenValue;

    public Token(
            @JsonProperty("tokenValue") String tokenValue
    ) {
        this.tokenValue = tokenValue;
    }
}
