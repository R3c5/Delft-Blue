package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * A DTO representing a DelftBlue cluster node.
 */
@Data
public class ClusterNodeDTO {


    Token token;
    OwnerName ownerName;
    URL url;
    Resource resources;

    /**
     * The constructor for the ClusterNodeDTO.
     * @param token the token of the node
     * @param ownerName the ownerName of the node
     * @param url the url of the node
     * @param resources the resources of the node
     */
    public ClusterNodeDTO(
            @JsonProperty("token") Token token,
            @JsonProperty("ownerName") OwnerName ownerName,
            @JsonProperty("url") URL url,
            @JsonProperty("resources") Resource resources
    ) {
        // validate NetID
        this.token = token;
        this.ownerName = ownerName;
        this.url = url;
        this.resources = resources;
    }
}
