package sem.faculty.domain.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import sem.commons.*;
import sem.commons.NotValidResourcesException;
import sem.faculty.controllers.ScheduleRequestController;
import sem.faculty.domain.*;
import sem.faculty.handler.FacultyHandler;
import sem.faculty.handler.FacultyHandlerService;
import sem.faculty.provider.CurrentTimeProvider;
import sem.faculty.provider.TimeProvider;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AcceptRequestsSchedulerTest {

    SchedulableRequestsScheduler scheduler;
    @Mock
    private final TimeProvider timeProvider = mock(CurrentTimeProvider.class);
    @Mock
    private final ScheduleRequestController controller = mock(ScheduleRequestController.class);

    @Mock
    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate = mock(KafkaTemplate.class);

    @Mock
    private final RequestRepository requestRepository = mock(RequestRepository.class);


    @BeforeEach
    void setUp() {
        scheduler = new AcceptRequestsScheduler(controller, requestRepository, kafkaTemplate);
    }

    @Test
    void saveRequestInFaculty() throws NotValidResourcesException, ExecutionException, InterruptedException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 17);
        RequestDetails rd = new RequestDetails("name", "description", date, RequestStatus.DROPPED);
        Request request = new Request(rd, "netId", FacultyName.ARCH, new Resource(5, 1, 1));

        Faculty faculty = mock(Faculty.class);
        when(controller.sendReserveResources(any())).thenReturn(new StatusDTO("OK"));

        scheduler.saveRequestInFaculty(request, faculty, date);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        verify(faculty).getFacultyName();
        verify(faculty, times(1)).scheduleForDate(request, date);
        verifyNoMoreInteractions(faculty);
    }

    @Test
    void acceptRequestFromDB() throws NotValidResourcesException, ExecutionException, InterruptedException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 17);
        RequestDetails rd = new RequestDetails("name", "description", date, RequestStatus.DROPPED);
        Request request = new Request(rd, "netId", FacultyName.ARCH, new Resource(5, 1, 1));

        Faculty faculty = mock(Faculty.class);
        when(controller.sendReserveResources(any())).thenReturn(new StatusDTO("OK"));
        when(requestRepository.findByRequestId(request.getRequestId())).thenReturn(request);

        scheduler.saveRequestInFaculty(request, faculty, date);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);

        verify(requestRepository, times(1)).findByRequestId(request.getRequestId());
        verify(requestRepository, times(1)).updateRequestStatusAccepted(request.getRequestId());
        verifyNoMoreInteractions(requestRepository);

        verify(faculty).getFacultyName();
        verify(faculty, times(1)).scheduleForDate(request, date);
        verifyNoMoreInteractions(faculty);
    }


    @Test
    void reserveResourceFalse() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 17);
        RequestDetails rd = new RequestDetails("name", "description", date, RequestStatus.DROPPED);
        Request request = new Request(rd, "netId", FacultyName.ARCH, new Resource(5, 1, 1));

        Faculty faculty = mock(Faculty.class);
        when(controller.sendReserveResources(any())).thenReturn(new StatusDTO("NOT ENOUGH RESOURCES AVAILABLE"));

        scheduler.saveRequestInFaculty(request, faculty, date);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.DENIED);

        verify(faculty).getFacultyName();
        verifyNoMoreInteractions(faculty);

        verify(kafkaTemplate, times(1)).send("publish-notification", new NotificationDTO(
                request.getRequestFacultyInformation().getNetId(),
                "Could not schedule request with name " +
                        request.getRequestResourceManagerInformation().getName() +
                        " because NOT ENOUGH RESOURCES AVAILABLE"));
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void denyRequestFromDB() throws NotValidResourcesException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 17);
        RequestDetails rd = new RequestDetails("name", "description", date, RequestStatus.DROPPED);
        Request request = new Request(rd, "netId", FacultyName.ARCH, new Resource(5, 1, 1));

        Faculty faculty = mock(Faculty.class);
        when(controller.sendReserveResources(any())).thenReturn(new StatusDTO("NOT ENOUGH RESOURCES AVAILABLE"));
        when(requestRepository.findByRequestId(request.getRequestId())).thenReturn(request);

        scheduler.saveRequestInFaculty(request, faculty, date);

        assertThat(request.getStatus()).isEqualTo(RequestStatus.DENIED);

        verify(requestRepository, times(2)).findByRequestId(request.getRequestId());
        verify(requestRepository, times(1)).delete(request);
        verifyNoMoreInteractions(requestRepository);


        verify(faculty).getFacultyName();
        verifyNoMoreInteractions(faculty);

        verify(kafkaTemplate, times(1)).send("publish-notification", new NotificationDTO(
                request.getRequestFacultyInformation().getNetId(),
                "Could not schedule request with name " +
                        request.getRequestResourceManagerInformation().getName() +
                        " because NOT ENOUGH RESOURCES AVAILABLE"));
        verifyNoMoreInteractions(kafkaTemplate);
    }
}