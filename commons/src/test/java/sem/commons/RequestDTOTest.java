package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class RequestDTOTest {
    long id;
    RequestDTO requestDTO;
    FacultyName faculty;
    String name;
    String netId;
    String descr;
    LocalDate date;
    Resource resource;

    @BeforeEach
    void createRequestDTO() throws NotValidResourcesException {
        id = 0L;
        name = "Name";
        netId = "NETID";
        faculty = FacultyName.EEMCS;
        descr = "Description";
        date = LocalDate.of(2022, 12, 5);
        resource = new Resource(1, 1, 1);
        requestDTO = new RequestDTO(id, name, netId, faculty, descr, date, resource);
    }

    @Test
    void testEmptyConstructor() {
        RequestDTO empty = new RequestDTO();
        assertThat(empty.getRequestId()).isEqualTo(0L);
        assertThat(empty.getRequestFacultyInformation()).isNull();
        assertThat(empty.getRequestResourceManagerInformation()).isNull();
    }

    @Test
    void testConstructor() {
        assertThat(requestDTO.getRequestId()).isEqualTo(0L);
        assertThat(requestDTO.getRequestResourceManagerInformation().getName()).isEqualTo(name);
        assertThat(requestDTO.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
        assertThat(requestDTO.getRequestFacultyInformation().getFaculty()).isEqualTo(faculty);
        assertThat(requestDTO.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
        assertThat(requestDTO.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
        assertThat(requestDTO.getRequestResourceManagerInformation().getResource()).isEqualTo(resource);
    }

    @Test
    void getRequestId() {
        assertThat(requestDTO.getRequestId()).isEqualTo(0L);
    }

    @Test
    void getNetId() {
        assertThat(requestDTO.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
    }

    @Test
    void getName() {
        assertThat(requestDTO.getRequestResourceManagerInformation().getName()).isEqualTo(name);
    }

    @Test
    void getFaculty() {
        assertThat(requestDTO.getRequestFacultyInformation().getFaculty()).isEqualTo(faculty);
    }

    @Test
    void getDescription() {
        assertThat(requestDTO.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
    }

    @Test
    void getPreferredDate() {
        assertThat(requestDTO.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
    }

    @Test
    void getResource() {
        assertThat(requestDTO.getRequestResourceManagerInformation().getResource()).isEqualTo(resource);
    }

    @Test
    void setNetId() {
        assertThat(requestDTO.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
        requestDTO.getRequestFacultyInformation().setNetId("NEW NETID");
        assertThat(requestDTO.getRequestFacultyInformation().getNetId()).isEqualTo("NEW NETID");
    }

    @Test
    void setName() {
        assertThat(requestDTO.getRequestResourceManagerInformation().getName()).isEqualTo(name);
        requestDTO.getRequestResourceManagerInformation().setName("NEW NAME");
        assertThat(requestDTO.getRequestResourceManagerInformation().getName()).isEqualTo("NEW NAME");
    }

    @Test
    void setDescription() {
        assertThat(requestDTO.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
        requestDTO.getRequestResourceManagerInformation().setDescription("NEW DESCRIPTION");
        assertThat(requestDTO.getRequestResourceManagerInformation().getDescription()).isEqualTo("NEW DESCRIPTION");
    }

    @Test
    void setPreferredDate() {
        assertThat(requestDTO.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
        LocalDate newDate = LocalDate.of(2022, 12, 6);
        requestDTO.getRequestFacultyInformation().setPreferredDate(newDate);
        assertThat(requestDTO.getRequestFacultyInformation().getPreferredDate()).isEqualTo(newDate);
    }
}
