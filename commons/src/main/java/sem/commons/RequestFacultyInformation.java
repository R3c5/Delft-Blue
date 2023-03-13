package sem.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Embeddable
public class RequestFacultyInformation {

    @Column(name = "preferredDate", nullable = false)
    private LocalDate preferredDate;
    @Convert(converter = FacultyNameAttributeConverter.class)
    @Column(name = "facultyName", nullable = false)
    private FacultyName faculty;
    @Column(name = "netId", nullable = false)
    private String netId;

    /**
     * The constructor for the RequestFacultyInformation.
     * @param preferredDate the preferred date of the request
     * @param faculty the faculty of the request
     * @param netId the netId of the owner of the request
     */
    public RequestFacultyInformation(LocalDate preferredDate, FacultyName faculty, String netId) {
        this.preferredDate = preferredDate;
        this.faculty = faculty;
        this.netId = netId;
    }
}
