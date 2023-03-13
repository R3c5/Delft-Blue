package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PendingRequestsDTO {
    private String status;
    private List<RequestDTO> requests;


    public PendingRequestsDTO(@JsonProperty("status") String status, @JsonProperty("requests") List<RequestDTO> requests) {
        this.status = status;
        this.requests = requests;
    }
}
