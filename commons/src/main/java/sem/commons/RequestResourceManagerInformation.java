package sem.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Data
@NoArgsConstructor
@Embeddable
public class RequestResourceManagerInformation {

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Embedded
    private Resource resource;

    /**
     * The constructor will all arguments for the RequestResourceManagerInformation.
     * @param name the name of the request
     * @param description the description of the request
     * @param resource the Resources of the requests
     */
    public RequestResourceManagerInformation(String name, String description, Resource resource) {
        this.name = name;
        this.description = description;
        this.resource = resource;
    }
}
