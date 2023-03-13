package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private String netId;
    private String password;
    private String role;
    private List<String> faculties;

    /**
     * Instantiates a new User dto.
     *
     * @param netId     the net id
     * @param password  the password
     * @param role      the role
     * @param faculties the faculties
     */
    public UserDTO(
            @JsonProperty("netId") String netId,
            @JsonProperty("password") String password,
            @JsonProperty("role") String role,
            @JsonProperty("faculties") List<String> faculties) {
        this.netId = netId;
        this.password = password;
        this.role = role;
        this.faculties = faculties;
    }
}
