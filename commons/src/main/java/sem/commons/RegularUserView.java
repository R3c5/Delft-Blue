package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class RegularUserView {

    private Map<FacultyNameDTO, Resource> resourcesPerFaculty;

    public RegularUserView(
            @JsonProperty("resourcesPerFaculty") Map<FacultyNameDTO, Resource> resourcesPerFaculty) {
        this.resourcesPerFaculty = resourcesPerFaculty;
    }


}
