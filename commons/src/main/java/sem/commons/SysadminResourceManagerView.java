package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SysadminResourceManagerView {

    private Map<FacultyNameDTO, Resource> availableResourcesPerFaculty;
    private Resource reservedResources;
    private List<ClusterNodeDTO> clusterNodeDTOList;

    /**
     * The constructor.
     * @param availableResourcesPerFaculty the available resources
     * @param reservedResources the reserved resources
     * @param clusterNodeDTOList the cluster list
     */
    public SysadminResourceManagerView(
            @JsonProperty("availableResourcesPerFaculty") Map<FacultyNameDTO, Resource> availableResourcesPerFaculty,
            @JsonProperty("reservedResourcesPerFaculty") Resource reservedResources,
            @JsonProperty("clusterNodeDTOList") List<ClusterNodeDTO> clusterNodeDTOList) {
        this.availableResourcesPerFaculty = availableResourcesPerFaculty;
        this.reservedResources = reservedResources;
        this.clusterNodeDTOList = clusterNodeDTOList;
    }

}
