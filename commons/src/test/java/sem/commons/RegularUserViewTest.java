package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;


class RegularUserViewTest {

    RegularUserView regularUserView;
    FacultyNameDTO facultyNameDTO;
    Resource resource;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        facultyNameDTO = new FacultyNameDTO("EEMCS");
        resource = new Resource(3, 2, 1);
        regularUserView = new RegularUserView(Map.of(facultyNameDTO, resource));
    }

    @Test
    void getResourcesPerFaculty() {
        Map<FacultyNameDTO, Resource> returned = regularUserView.getResourcesPerFaculty();
        assertThat(returned).isEqualTo(Map.of(facultyNameDTO, resource));
    }

    @Test
    void setResourcesPerFaculty() {
        regularUserView = new RegularUserView(Map.of());
        regularUserView.setResourcesPerFaculty(Map.of(facultyNameDTO, resource));
        Map<FacultyNameDTO, Resource> returned = regularUserView.getResourcesPerFaculty();
        assertThat(returned).isEqualTo(Map.of(facultyNameDTO, resource));
    }

    @Test
    void testEquals() {
        RegularUserView regularUserView1 = new RegularUserView(Map.of(facultyNameDTO, resource));
        assertThat(regularUserView).isEqualTo(regularUserView1);
    }

    @Test
    void testHashCode() {
        RegularUserView regularUserView1 = new RegularUserView(Map.of(facultyNameDTO, resource));
        assertThat(regularUserView1.hashCode()).isEqualTo(regularUserView.hashCode());
    }
}