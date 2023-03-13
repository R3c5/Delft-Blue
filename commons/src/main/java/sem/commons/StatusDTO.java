package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatusDTO {

    private String status;

    public StatusDTO(@JsonProperty("status") String status) {
        this.status = status;
    }
}
