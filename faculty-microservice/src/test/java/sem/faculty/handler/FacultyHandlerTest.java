package sem.faculty.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import sem.commons.NotValidResourcesException;
import sem.commons.*;
import sem.faculty.controllers.ScheduleRequestController;
import sem.faculty.domain.*;
import sem.faculty.domain.scheduler.AcceptRequestsScheduler;
import sem.faculty.domain.scheduler.DenyRequestsScheduler;
import sem.faculty.domain.scheduler.PendingRequestsScheduler;
import sem.faculty.domain.scheduler.Scheduler;
import sem.faculty.provider.CurrentTimeProvider;
import sem.faculty.provider.TimeProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FacultyHandlerTest {

    FacultyHandler facultyHandler;
    @Mock
    private final TimeProvider timeProvider = mock(CurrentTimeProvider.class);
    @Mock
    private final ScheduleRequestController scheduleRequestController = mock(ScheduleRequestController.class);
    @Mock
    private final RequestRepository requestRepository = mock(RequestRepository.class);

    @Mock
    private transient KafkaTemplate<String, NotificationDTO> kafkaTemplate = mock(KafkaTemplate.class);


    @BeforeEach
    void setUp() {
        facultyHandler = new FacultyHandler(kafkaTemplate);
        facultyHandler.timeProvider = timeProvider;
        facultyHandler.requestRepository = requestRepository;
        facultyHandler.scheduleRequestController = scheduleRequestController;
    }

    @Test
    void newFacultyHandler() {
        FacultyHandler facultyHandler1 = new FacultyHandler(kafkaTemplate);
        Map<FacultyName, Faculty> faculties = facultyHandler1.faculties;

        for (FacultyName fn : FacultyName.values()) {
            assertNotNull(faculties.get(fn));
        }
    }

    @Test
    void newFacultyHandlerAllArgsConstructor() {
        Map<FacultyName, Faculty> map = new HashMap<>();
        Scheduler scheduler = new AcceptRequestsScheduler(
                scheduleRequestController, requestRepository, kafkaTemplate);
        FacultyHandler facultyHandler1 = new FacultyHandler(
                map, scheduler, timeProvider, requestRepository, scheduleRequestController, kafkaTemplate);
        Map<FacultyName, Faculty> faculties = facultyHandler1.faculties;
        assertNotNull(facultyHandler1);
    }

    @Test
    void handleIncomingRequestsDenyScheduler() throws NotValidResourcesException {
        LocalDate today = LocalDate.of(2022, Month.DECEMBER, 15);
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 14);
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        when(timeProvider.getCurrentDate()).thenReturn(today);
        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(DenyRequestsScheduler.class);
    }

    @Test
    void handleIncomingRequestsPendingScheduler()
            throws NotValidResourcesException, ExecutionException, InterruptedException {
        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 14);
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 14, 12, 0);

        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 15);
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        facultyHandler.requestRepository.save(request);
        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        when(scheduleRequestController.sendScheduleRequest(any()))
                .thenReturn(ResponseEntity.ok(todayDate));
        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(PendingRequestsScheduler.class);
    }

    @Test
    void getPendingRequests() {
        Faculty faculty = new Faculty(FacultyName.EEMCS, new CurrentTimeProvider());
        facultyHandler.faculties.put(FacultyName.EEMCS, faculty);

        assertEquals(facultyHandler.getPendingRequests(FacultyName.EEMCS), new ArrayList<>());
    }

    @Test
    void getPendingRequestsEmptyListReturned() throws NotValidResourcesException {
        Faculty faculty = new Faculty(FacultyName.EEMCS, new CurrentTimeProvider());
        Request facultyRequest = new Request(
                new RequestDetails(
                        "name1",
                        "desc1",
                        LocalDate.of(2022, 1, 1),
                        RequestStatus.PENDING
                ),
                "netId1",
                FacultyName.EEMCS,
                new Resource(100, 80, 80)
        );
        faculty.addPendingRequest(facultyRequest);
        when(requestRepository.findByRequestId(0)).thenReturn(facultyRequest);

        facultyHandler.faculties.put(FacultyName.EEMCS, faculty);

        assertThat(facultyHandler.getPendingRequests(FacultyName.EEMCS)).isEqualTo(List.of(facultyRequest));
    }

    //Tests for not accepting anymore 5 minutes before the preferred day starts.
    @Test
    void handleIncomingRequestsTwoMinutesBeforePreferredDate() throws NotValidResourcesException {
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 12, 23, 58);
        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 12);

        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 13);
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));


        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(DenyRequestsScheduler.class);
    }

    @Test
    void handleIncomingRequestsFiveMinutesBeforePreferredDate() throws NotValidResourcesException {
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 12, 23, 55);
        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 12);

        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 13);
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));


        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(DenyRequestsScheduler.class);
    }

    //Tests for accepting requests within 6 hours before the preferred day
    @Test
    void handleIncomingRequestsSixHoursBeforePreferredDate() throws NotValidResourcesException,
            ExecutionException, InterruptedException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 13); //Preferred LocalDate for the request
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));


        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 12);
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 12, 18, 0);

        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        when(scheduleRequestController.sendScheduleRequest(any()))
                .thenReturn(ResponseEntity.ok(todayDate));
        when(scheduleRequestController.sendReserveResources(any())).thenReturn(new StatusDTO("OK"));

        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(AcceptRequestsScheduler.class);
    }

    @Test
    void handleIncomingRequestsSevenHoursBeforePreferredDate() throws NotValidResourcesException,
            ExecutionException, InterruptedException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 13); //Preferred LocalDate for the request
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 12);
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 12, 17, 59);

        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        when(scheduleRequestController.sendScheduleRequest(any()))
                .thenReturn(ResponseEntity.ok(todayDate));

        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(PendingRequestsScheduler.class);
    }

    @Test
    void handleIncomingRequestsOneHourBeforePreferredDate() throws NotValidResourcesException,
            ExecutionException, InterruptedException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 13); //Preferred LocalDate for the request
        RequestDetails rd = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        LocalDate todayDate = LocalDate.of(2022, Month.DECEMBER, 12);
        LocalDateTime todayDateTime = LocalDateTime.of(2022, Month.DECEMBER, 12,
                23, 54, 59);

        when(timeProvider.getCurrentDate()).thenReturn(todayDate);
        when(timeProvider.getCurrentDateTime()).thenReturn(todayDateTime);

        when(scheduleRequestController.sendReserveResources(any())).thenReturn(new StatusDTO("OK"));

        when(scheduleRequestController.sendScheduleRequest(any()))
                .thenReturn(ResponseEntity.ok(todayDate));
        facultyHandler.handleIncomingRequests(request);
        assertThat(facultyHandler.scheduler.getClass()).isEqualTo(AcceptRequestsScheduler.class);
    }

    @Test
    void getRequestForDate() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2015, 2, 3);
        RequestDetails rd = new RequestDetails("Name1", "Description",
                LocalDate.of(2015, 2, 4), RequestStatus.ACCEPTED);
        Request request = new Request(rd, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        RequestDTO requestDTO = new RequestDTO(request.getRequestId(),
                request.getRequestResourceManagerInformation().getName(),
                request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getDescription(),
                request.getRequestFacultyInformation().getPreferredDate(),
                request.getRequestResourceManagerInformation().getResource());

        facultyHandler.faculties.get(FacultyName.EEMCS).getSchedule().put(date, List.of(request));
        Map<FacultyName, List<RequestDTO>> map = facultyHandler.getRequestForDate(date);
        assertEquals(map.get(FacultyName.EEMCS).get(0), requestDTO);
    }


    @Test
    void getPendingRequestsForTomorrow() throws NotValidResourcesException {
        LocalDate tomorrow = LocalDate.of(2015, 2, 3);
        LocalDate date = LocalDate.of(2015, 2, 4);
        RequestDetails rd1 = new RequestDetails("Name1", "Description", tomorrow, RequestStatus.ACCEPTED);
        Request request1 = new Request(rd1, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        request1.setRequestId(1L);
        RequestDetails rd2 = new RequestDetails("Name1", "Description", date, RequestStatus.ACCEPTED);
        Request request2 = new Request(rd2, "NetId", FacultyName.EEMCS, new Resource(1, 1, 1));

        request2.setRequestId(2L);
        Faculty faculty = facultyHandler.faculties.get(FacultyName.EEMCS);
        faculty.addPendingRequest(request1);
        faculty.addPendingRequest(request2);

        when(timeProvider.getCurrentDate()).thenReturn(tomorrow.minusDays(1));
        when(requestRepository.findByRequestId(1L)).thenReturn(request1);
        when(requestRepository.findByRequestId(2L)).thenReturn(request2);
        List<Request> list = facultyHandler.getPendingRequestsForTomorrow(faculty);
        assertThat(list).isEqualTo(List.of(request1));
        assertThat(faculty.getPendingRequests()).isEqualTo(List.of(2L));
    }

    @Test
    void getCurrentDate() {
        when(facultyHandler.getCurrentDate()).thenReturn(LocalDate.of(2015, 2, 3));
        LocalDate ret = facultyHandler.getCurrentDate();
        verify(timeProvider, times(1)).getCurrentDate();
        assertEquals(ret, LocalDate.of(2015, 2, 3));
    }
}