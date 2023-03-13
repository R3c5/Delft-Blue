package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FacultyNameDTO {
    private String facultyName;

    public FacultyNameDTO(@JsonProperty("facultyName") String facultyName) {
        this.facultyName = facultyName;
    }
}
