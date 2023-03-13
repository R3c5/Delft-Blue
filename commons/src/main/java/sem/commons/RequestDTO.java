package sem.commons;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO for the request class, containing only the attributes and no logic.
 * The status was also omitted, because the status is only stored
 * in the database and not needed in between microservices.
 */
@Data
@NoArgsConstructor
public class RequestDTO {

    private long requestId;
    private RequestFacultyInformation requestFacultyInformation;
    private RequestResourceManagerInformation requestResourceManagerInformation;

    /**
     * secondary constructor without requestID.
     * @param name - the request name
     * @param netId - the user NetId
     * @param faculty - the faculty to send the request to
     * @param description - the description
     * @param preferredDate - the preferred date to schedule the request
     * @param resource - the resource required for the request.
     */
    public RequestDTO(@JsonProperty("name") String name,
                      @JsonProperty("netId") String netId,
                      @JsonProperty("faculty") FacultyName faculty,
                      @JsonProperty("description") String description,
                      @JsonProperty("preferredDate") LocalDate preferredDate,
                      @JsonProperty("resource") Resource resource) {
        this.requestResourceManagerInformation = new RequestResourceManagerInformation(name, description, resource);
        this.requestFacultyInformation = new RequestFacultyInformation(preferredDate, faculty, netId);
    }


    /**
     * This is the constructor that takes all arguments to then fill its own parameters.
     * @param requestId the Id of the request
     * @param name - the request name
     * @param netId - the user NetId
     * @param faculty - the faculty to send the request to
     * @param description - the description
     * @param preferredDate - the preferred date to schedule the request
     * @param resource - the resource required for the request.
     */
    public RequestDTO(
                      long requestId,
                      String name,
                      String netId,
                      FacultyName faculty,
                      String description,
                      LocalDate preferredDate,
                      Resource resource) {
        this.requestId = requestId;
        this.requestResourceManagerInformation = new RequestResourceManagerInformation(name, description, resource);
        this.requestFacultyInformation = new RequestFacultyInformation(preferredDate, faculty, netId);
    }

}




