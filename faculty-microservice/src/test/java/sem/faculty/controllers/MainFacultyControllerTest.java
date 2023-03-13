package sem.faculty.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sem.commons.*;
import sem.faculty.handler.FacultyHandlerService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class MainFacultyControllerTest {

    private FacultyHandlerService facultyHandlerService;

    private MainFacultyController mainFacultyController;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        facultyHandlerService = Mockito.mock(FacultyHandlerService.class);
        mainFacultyController = new MainFacultyController(facultyHandlerService);

        //Test System.out.print statements
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void getPendingRequests() {
        mainFacultyController.getPendingRequests(new FacultyNameDTO("EEMCS"));
        verify(facultyHandlerService, times(1)).getPendingRequests("EEMCS");
    }

    @Test
    void acceptRequests() {
        AcceptRequestsDTO acceptRequestsDTO = new AcceptRequestsDTO("EEMCS", List.of(1L));
        mainFacultyController.acceptRequests(acceptRequestsDTO);
        verify(facultyHandlerService, times(1)).acceptRequests("EEMCS", acceptRequestsDTO.getAcceptedRequests());
    }

    @Test
    void getScheduleForDate() {
        DateDTO dateDTO = new DateDTO(2015, 2, 3);
        mainFacultyController.getScheduleForDate(dateDTO);
        verify(facultyHandlerService, times(1))
                .getScheduleForDate(argThat(x -> x.equals(LocalDate.of(2015, 2, 3))));
    }

    @Test
    void listenerTest() throws NotValidResourcesException {
        RequestDTO req = new RequestDTO(1, "Name", "NetID", FacultyName.AE,
                "Descr", LocalDate.of(2022, 1, 18),
                new Resource(1, 1, 1));
        StatusDTO expected = new StatusDTO("OK");

        when(facultyHandlerService.requestListener(req)).thenReturn(expected);

        StatusDTO status = mainFacultyController.listener(req);

        assertThat(status).isEqualTo(expected);
        verify(facultyHandlerService, times(1)).requestListener(req);
    }

    @Test
    void acceptRequestsOKAndPrintln() {
        StatusDTO expected = new StatusDTO("OK");
        AcceptRequestsDTO acceptRequestsDTO = new AcceptRequestsDTO("EEMCS", List.of(1L));
        when(facultyHandlerService.acceptRequests("EEMCS", List.of(1L)))
                .thenReturn(expected);

        StatusDTO statusDTO = mainFacultyController.acceptRequests(acceptRequestsDTO);

        assertThat("got a list of request IDs to accept [1] for faculty EEMCS")
                .isEqualTo(outputStreamCaptor.toString().trim());
        assertThat(statusDTO).isEqualTo(expected);
        verify(facultyHandlerService, times(1)).acceptRequests("EEMCS", acceptRequestsDTO.getAcceptedRequests());
        System.out.println(statusDTO.toString());
    }

    @Test
    void getCorrectPendingRequestsAndPrintln() throws NotValidResourcesException {
        RequestDTO req = new RequestDTO(1, "Name", "NetID", FacultyName.AE,
                "Descr", LocalDate.of(2022, 1, 18),
                new Resource(1, 1, 1));
        PendingRequestsDTO expected = new PendingRequestsDTO("OK", List.of(req));
        when(facultyHandlerService.getPendingRequests("EEMCS")).thenReturn(expected);
        PendingRequestsDTO pending = mainFacultyController.getPendingRequests(new FacultyNameDTO("EEMCS"));

        assertThat("got a faculty name EEMCS").isEqualTo(outputStreamCaptor.toString().trim());
        assertThat(pending).isEqualTo(expected);
        verify(facultyHandlerService, times(1)).getPendingRequests("EEMCS");
    }

    @Test
    void getScheduleForDateIsNotChanged() {
        DateDTO dateDTO = new DateDTO(2015, 2, 3);
        LocalDate date = LocalDate.of(2015, 2, 3);
        SysadminScheduleDTO expected = new SysadminScheduleDTO(new HashMap<>());

        when(facultyHandlerService.getScheduleForDate(date)).thenReturn(expected);

        SysadminScheduleDTO schedule = mainFacultyController.getScheduleForDate(dateDTO);

        assertThat(schedule).isEqualTo(expected);
        verify(facultyHandlerService, times(1))
                .getScheduleForDate(argThat(x -> x.equals(LocalDate.of(2015, 2, 3))));
    }
}