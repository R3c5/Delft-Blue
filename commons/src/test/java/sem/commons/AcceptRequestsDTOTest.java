package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcceptRequestsDTOTest {

    AcceptRequestsDTO acceptRequestsDTO;
    List<Long> list;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        list = List.of(1L);
        acceptRequestsDTO = new AcceptRequestsDTO("EEMCS", list);
    }

    @Test
    void notNull() {
        assertNotNull(acceptRequestsDTO);
    }

    @Test
    void getFacultyName() {
        assertEquals("EEMCS", acceptRequestsDTO.getFacultyName());
    }

    @Test
    void getAcceptedRequests() {
        assertEquals(list, acceptRequestsDTO.getAcceptedRequests());
    }

    @Test
    void setFacultyName() {
        acceptRequestsDTO.setFacultyName("AE");
        assertEquals("AE", acceptRequestsDTO.getFacultyName());
    }

    @Test
    void setAcceptedRequests() throws NotValidResourcesException {
        List<Long> list2 = List.of(2L);
        acceptRequestsDTO.setAcceptedRequests(list2);
        assertEquals(list2, acceptRequestsDTO.getAcceptedRequests());
    }

    @Test
    void testEquals() throws NotValidResourcesException {
        List<Long> list2 = List.of(1L);
        AcceptRequestsDTO acceptRequestsDTO2 = new AcceptRequestsDTO("EEMCS", list2);
        assertEquals(acceptRequestsDTO2, acceptRequestsDTO);
    }

    @Test
    void canEqual() {
        AcceptRequestsDTO acceptRequestsDTO2 = new AcceptRequestsDTO("EEMCS", list);
        assertTrue(acceptRequestsDTO.canEqual(acceptRequestsDTO2));
    }

    @Test
    void testToString() {
        System.out.println(acceptRequestsDTO.toString());
        assertEquals("AcceptRequestsDTO(facultyName=EEMCS, acceptedRequests=[1])",
                acceptRequestsDTO.toString());
    }
}