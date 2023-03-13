package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenDTO {

    String status;
    String token;

    public TokenDTO(@JsonProperty("status") String status, @JsonProperty("token") String token) {
        this.status = status;
        this.token = token;
    }
}
