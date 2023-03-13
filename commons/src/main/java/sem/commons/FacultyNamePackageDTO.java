package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FacultyNamePackageDTO {

    private List<FacultyNameDTO> faculties;

    public FacultyNamePackageDTO(
            @JsonProperty("faculties") List<FacultyNameDTO> faculties
    ) {
        this.faculties = faculties;
    }

    public FacultyNamePackageDTO(String data) {
        this.faculties = Arrays.stream(data.substring(1, data.length() - 1).split(", ")).map(FacultyNameDTO::new)
                .collect(Collectors.toList());
    }
}
