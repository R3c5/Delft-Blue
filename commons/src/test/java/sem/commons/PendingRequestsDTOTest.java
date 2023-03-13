package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PendingRequestsDTOTest {

    PendingRequestsDTO pendingRequestsDTO;
    List<RequestDTO> list;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        list = List.of(new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1)));
        pendingRequestsDTO = new PendingRequestsDTO("OK", list);
    }

    @Test
    void notNull() {
        assertNotNull(pendingRequestsDTO);
    }

    @Test
    void getStatus() {
        assertEquals("OK", pendingRequestsDTO.getStatus());
    }

    @Test
    void getRequests() {
        assertEquals(list, pendingRequestsDTO.getRequests());
    }

    @Test
    void setStatus() {
        pendingRequestsDTO.setStatus("WRONG");
        assertEquals("WRONG", pendingRequestsDTO.getStatus());
    }

    @Test
    void setRequests() throws NotValidResourcesException {
        List<RequestDTO> list2 = List.of(new RequestDTO(2L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1)));
        pendingRequestsDTO.setRequests(list2);
        assertEquals(list2, pendingRequestsDTO.getRequests());
    }

    @Test
    void testEquals() {
        PendingRequestsDTO pendingRequestsDTO2 = new PendingRequestsDTO("OK", list);
        assertEquals(pendingRequestsDTO, pendingRequestsDTO2);
    }

    @Test
    void canEqual() {
        PendingRequestsDTO pendingRequestsDTO2 = new PendingRequestsDTO("OK", list);
        assertTrue(pendingRequestsDTO.canEqual(pendingRequestsDTO2));
    }

    @Test
    void testToString() {
        System.out.println(this.toString());
        assertEquals("PendingRequestsDTO(status=OK, requests=[RequestDTO(requestId=1, " +
                        "requestFacultyInformation=RequestFacultyInformation(preferredDate=2015-02-03, " +
                        "faculty=EEMCS, netId=netId), requestResourceManagerInformation=" +
                        "RequestResourceManagerInformation(name=name, description=desc, " +
                        "resource=Resource(cpu=1, gpu=1, memory=1)))])",
                pendingRequestsDTO.toString());
    }
}