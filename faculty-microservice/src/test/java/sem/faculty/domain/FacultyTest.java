package sem.faculty.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.Mock;
import sem.commons.FacultyName;
import sem.commons.RequestDTO;
import sem.commons.Resource;
import sem.commons.NotValidResourcesException;
import sem.faculty.provider.CurrentTimeProvider;
import sem.faculty.provider.TimeProvider;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

class FacultyTest {

    @Mock
    private final TimeProvider timeProvider = mock(CurrentTimeProvider.class);
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        faculty = new Faculty(FacultyName.EEMCS, timeProvider);
    }


    @Test
    void scheduleDate_creates_new_list() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 7);
        RequestDetails rd = new RequestDetails("Name1", "Desription", date, RequestStatus.ACCEPTED);
        Request request1 = new Request(rd, "NetID", FacultyName.EEMCS, new Resource(1, 1, 1));

        Map<LocalDate, List<Request>> expected = new HashMap<>();
        expected.put(date, List.of(request1));

        faculty.scheduleForDate(request1, date);
        assertThat(faculty.getSchedule()).isEqualTo(expected);
    }

    @Test
    void scheduleDate_add_to_list() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 7);
        RequestDetails rd = new RequestDetails("Name1", "Desription", date, RequestStatus.ACCEPTED);
        Request request1 = new Request(rd, "NetID", FacultyName.EEMCS, new Resource(1, 1, 1));

        RequestDetails rd2 = new RequestDetails("Name2", "Desription", date, RequestStatus.ACCEPTED);
        Request request2 = new Request(rd2, "NetID", FacultyName.EEMCS, new Resource(1, 1, 1));

        Map<LocalDate, List<Request>> expected = new HashMap<>();
        expected.put(date, List.of(request1, request2));

        faculty.scheduleForDate(request1, date);
        faculty.scheduleForDate(request2, date);
        assertThat(faculty.getSchedule()).isEqualTo(expected);
    }

    @Test
    void addPendingRequest() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 7);
        RequestDetails rd = new RequestDetails("Name1", "Desription", date, RequestStatus.ACCEPTED);
        Request request1 = new Request(rd, "NetID", FacultyName.EEMCS, new Resource(1, 1, 1));

        assertThat(faculty.getPendingRequests()).isEmpty();
        faculty.addPendingRequest(request1);
        List<Long> queue = faculty.getPendingRequests();
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.get(0)).isEqualTo(request1.getRequestId());
    }

    @Test
    void getRequestsForDate() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 7);
        RequestDetails rd = new RequestDetails("Name1", "Desription", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetID", FacultyName.EEMCS, new Resource(1, 1, 1));
        RequestDTO requestDTO = new RequestDTO(request.getRequestId(),
                request.getRequestResourceManagerInformation().getName(),
                request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getDescription(),
                request.getRequestFacultyInformation().getPreferredDate(),
                request.getRequestResourceManagerInformation().getResource());
            
        faculty.getSchedule().put(date, List.of(request));

        List<RequestDTO> list = faculty.getRequestsForDate(date);
        Assertions.assertEquals(requestDTO, list.get(0));
    }
}
