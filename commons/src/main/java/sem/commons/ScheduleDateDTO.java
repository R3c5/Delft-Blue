package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO representing the request made by a faculty to the resource manager
 * to fit a request on or before a given date.
 */
@Data
public class ScheduleDateDTO {
    Resource resources;
    LocalDate endDate;
    FacultyName facultyName;

    /**
     * Basic constructor.
     * @param resources the resources
     * @param endDate the endDate
     * @param facultyName the faculty name
     */
    public ScheduleDateDTO(
            @JsonProperty("resources") Resource resources,
            @JsonProperty("endDate") LocalDate endDate,
            @JsonProperty("facultyName") FacultyName facultyName) {
        this.resources = resources;
        this.endDate = endDate;
        this.facultyName = facultyName;
    }
}
