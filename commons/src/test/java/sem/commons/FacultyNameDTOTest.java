package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FacultyNameDTOTest {

    FacultyNameDTO facultyName;

    @BeforeEach
    void setUp() {
        facultyName = new FacultyNameDTO("EEMCS");
    }

    @Test
    void notNullConstructor() {
        assertNotNull(facultyName);
    }

    @Test
    void getFacultyName() {
        assertEquals("EEMCS", facultyName.getFacultyName());
    }

    @Test
    void setFacultyName() {
        facultyName.setFacultyName("AS");
        assertEquals("AS", facultyName.getFacultyName());
    }

    @Test
    void testEquals() {
        FacultyNameDTO facultyName2 = new FacultyNameDTO("EEMCS");
        assertEquals(facultyName, facultyName2);
    }

    @Test
    void canEqual() {
        FacultyNameDTO facultyName2 = new FacultyNameDTO("EEMCS");
        assertTrue(facultyName.canEqual(facultyName2));
    }

    @Test
    void testToString() {
        assertEquals("FacultyNameDTO(facultyName=EEMCS)", facultyName.toString());
    }
}