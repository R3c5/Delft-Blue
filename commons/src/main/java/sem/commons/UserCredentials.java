package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The type User credentials.
 */
@Data
public class UserCredentials {

    private String netId;

    private String password;


    /**
     * Instantiates a new User credentials.
     *
     * @param netId    the net id
     * @param password the password
     */
    public UserCredentials(@JsonProperty("netId") String netId, @JsonProperty("password") String password) {
        this.netId = netId;
        this.password = password;
    }
}
