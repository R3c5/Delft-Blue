package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

class FacultyNamePackageDTOTest {

    FacultyNameDTO facultyNameDTO1;
    FacultyNameDTO facultyNameDTO2;
    FacultyNamePackageDTO facultyNamePackageDTO;


    @BeforeEach
    void setUp() {
        facultyNameDTO1 = new FacultyNameDTO("EEMCS");
        facultyNameDTO2 = new FacultyNameDTO("AE");
        facultyNamePackageDTO = new FacultyNamePackageDTO(List.of(facultyNameDTO1, facultyNameDTO2));
    }

    @Test
    void readConstructor() {
        FacultyNamePackageDTO empty = new FacultyNamePackageDTO("[]");
        FacultyNamePackageDTO one = new FacultyNamePackageDTO("[EEMCS]");
        FacultyNamePackageDTO two = new FacultyNamePackageDTO("[EEMCS, AE]");

        assertThat(empty).isNotEqualTo(one);
        assertThat(one).isEqualTo(one);
        assertThat(two).isEqualTo(facultyNamePackageDTO);
    }

    @Test
    void getFaculties() {
        assertThat(facultyNamePackageDTO.getFaculties())
                .isEqualTo(new ArrayList<>(List.of(facultyNameDTO1, facultyNameDTO2)));
    }

    @Test
    void setFaculties() {
        facultyNamePackageDTO.setFaculties(List.of());
        assertThat(facultyNamePackageDTO.getFaculties()).isEqualTo(List.of());
    }

    @Test
    void testEquals() {
        assertThat(new FacultyNamePackageDTO(List.of(facultyNameDTO1, facultyNameDTO2)))
                .isEqualTo(facultyNamePackageDTO);
    }

    @Test
    void testHashCode() {
        assertThat(new FacultyNamePackageDTO(List.of(facultyNameDTO1, facultyNameDTO2)).hashCode())
                .isEqualTo(facultyNamePackageDTO.hashCode());
    }
}