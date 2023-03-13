package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SysadminResourceManagerViewTest {

    private Map<FacultyNameDTO, Resource> availableResourcesPerFaculty;
    private Resource reservedResources;
    private List<ClusterNodeDTO> clusterNodeDTOList;
    private SysadminResourceManagerView sysadminResourceManagerView;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        availableResourcesPerFaculty = new HashMap<>();
        reservedResources = new Resource(3, 2, 1);
        clusterNodeDTOList = new ArrayList<>();
        sysadminResourceManagerView =
               new SysadminResourceManagerView(availableResourcesPerFaculty, reservedResources, clusterNodeDTOList);
    }

    @Test
    void notNull() {
        assertNotNull(sysadminResourceManagerView);
        assertEquals(availableResourcesPerFaculty, sysadminResourceManagerView.getAvailableResourcesPerFaculty());
        assertEquals(reservedResources, sysadminResourceManagerView.getReservedResources());
        assertEquals(clusterNodeDTOList, sysadminResourceManagerView.getClusterNodeDTOList());
    }
}