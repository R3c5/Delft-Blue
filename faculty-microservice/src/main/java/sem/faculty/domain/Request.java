package sem.faculty.domain;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sem.commons.FacultyName;
import sem.commons.RequestFacultyInformation;
import sem.commons.RequestResourceManagerInformation;
import sem.commons.Resource;

/**
 * A DDD entity representing an request in our domain.
 */
@Entity
@Table(name = "requests")
@NoArgsConstructor
@Getter
public class Request {

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "requestId", nullable = false)
    private long requestId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Embedded
    protected RequestFacultyInformation requestFacultyInformation;

    @Embedded
    protected RequestResourceManagerInformation requestResourceManagerInformation;

    /**
     * Constructor method.
     */
    public Request(@JsonProperty("requestDetails") RequestDetails requestDetails,
                   @JsonProperty("nedId") String netId,
                   @JsonProperty("facultyName") FacultyName facultyName,
                   @JsonProperty("resource") Resource resource) {
        this.status = requestDetails.getStatus();
        this.requestResourceManagerInformation = new RequestResourceManagerInformation(
            requestDetails.getName(), requestDetails.getDescription(), resource);
        this.requestFacultyInformation = new RequestFacultyInformation(
                requestDetails.getPreferredDate(), facultyName, netId);
    }

    /**
     * Return the faculty for the request as a string.
     *
     * @return String with faculty
     */
    public String facultyString() {
        String faculties = this.requestFacultyInformation.getFaculty().toString();
        faculties = faculties.replace("[", "");
        faculties = faculties.replace("]", "");
        return faculties;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", status=" + status +
                ", netId='" + requestFacultyInformation.getNetId() +
                "', name='" + requestResourceManagerInformation.getName() +
                "', description='" + requestResourceManagerInformation.getDescription() +
                "', preferredDate=" + requestFacultyInformation.getPreferredDate() +
                ", faculty=" + requestFacultyInformation.getFaculty() +
                ", resource='" + requestResourceManagerInformation.getResource() +
                "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request) o;
        return requestId == request.requestId
                && status == request.status
                && requestFacultyInformation.equals(request.requestFacultyInformation)
                && requestResourceManagerInformation.equals(request.requestResourceManagerInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, status, requestFacultyInformation, requestResourceManagerInformation);
    }

}
