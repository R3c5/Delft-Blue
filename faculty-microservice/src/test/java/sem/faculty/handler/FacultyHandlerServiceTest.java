package sem.faculty.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import sem.commons.*;
import sem.commons.NotValidResourcesException;
import sem.faculty.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FacultyHandlerServiceTest {

    private FacultyHandler facultyHandler;
    private FacultyHandlerService facultyHandlerService;
    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        facultyHandler = Mockito.mock(FacultyHandler.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        facultyHandlerService = new FacultyHandlerService(facultyHandler, requestRepository);
    }


    @Test
    void requestListenerCorrect() throws NotValidResourcesException {
        RequestDTO requestDTO = new RequestDTO("name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2024, 2, 3), new Resource(1, 1, 1));

        when(facultyHandler.getCurrentDate()).thenReturn(LocalDate.of(2023, 1, 18));

        StatusDTO statusDTO = facultyHandlerService.requestListener(requestDTO);
        assertEquals("OK", statusDTO.getStatus());
        verify(facultyHandler, times(1)).handleIncomingRequests(
                argThat(x -> x.getRequestFacultyInformation().getNetId().equals("netId") &&
                        x.getRequestFacultyInformation().getFaculty().equals(FacultyName.EEMCS)));
    }

    @Test
    void requestListenerCatchManuallyInjectedMutant() throws NotValidResourcesException {
        RequestDTO requestDTO = new RequestDTO("name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1));

        when(facultyHandler.getCurrentDate()).thenReturn(LocalDate.of(2023, 1, 18));

        StatusDTO statusDTO = facultyHandlerService.requestListener(requestDTO);
        assertEquals("You cannot schedule requests for today or the past!", statusDTO.getStatus());
    }

    @Test
    void requestListenerWrongResources() throws NotValidResourcesException {
        Resource res = new Resource(1, 1, 1);
        res.setGpu(10);
        RequestDTO requestDTO = new RequestDTO("name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2024, 2, 3), res);

        when(facultyHandler.getCurrentDate()).thenReturn(LocalDate.of(2023, 1, 18));

        StatusDTO statusDTO = facultyHandlerService.requestListener(requestDTO);
        assertEquals("The cpu resources should be equal to at least max(memory, gpu)", statusDTO.getStatus());
    }

    @Test
    void deleteRequestCorrect() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));
        request.setRequestId(1L);

        when(requestRepository.findByRequestId(1L)).thenReturn(request);
        facultyHandlerService.deleteRequest(request);
        verify(requestRepository, times(1)).delete(request);
    }

    @Test
    void deleteRequestCorrect2() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));
        request.setRequestId(1L);

        Request request2 = new Request(rd, "netId2", FacultyName.AE, new Resource(1, 1, 1));
        request2.setRequestId(2L);

        when(requestRepository.findByRequestId(1L)).thenReturn(request2);
        facultyHandlerService.deleteRequest(request);
        verify(requestRepository, times(0)).delete(request);
        verify(requestRepository, times(0)).delete(request2);
    }

    @Test
    void notUsedAcceptRequest() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));
        request.setRequestId(1L);

        when(requestRepository.findByRequestId(1L)).thenReturn(request);
        facultyHandlerService.acceptRequest(request);
        verify(requestRepository, times(1)).updateRequestStatusAccepted(1L);
    }

    @Test
    void notUsedAcceptRequestElseCase() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));
        request.setRequestId(1L);

        Request request2 = new Request(rd, "netId2", FacultyName.AE, new Resource(1, 1, 1));
        request2.setRequestId(2L);

        when(requestRepository.findByRequestId(1L)).thenReturn(request2);
        facultyHandlerService.acceptRequest(request);
        verify(requestRepository, times(0)).updateRequestStatusAccepted(2L);
        verify(requestRepository, times(1)).saveAndFlush(request);
    }

    @Test
    void getPendingRequestsCorrect() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));

        List<Request> list = List.of(request);
        when(facultyHandler.getPendingRequests(FacultyName.EEMCS)).thenReturn(list);
        PendingRequestsDTO pendingRequestsDTO = facultyHandlerService.getPendingRequests("EEMCS");
        assertEquals("OK", pendingRequestsDTO.getStatus());
        RequestDTO requestDTO = pendingRequestsDTO.getRequests().get(0);
        assertEquals("name", requestDTO.getRequestResourceManagerInformation().getName());
        assertEquals(FacultyName.EEMCS, requestDTO.getRequestFacultyInformation().getFaculty());
        assertEquals("netId", requestDTO.getRequestFacultyInformation().getNetId());
    }

    @Test
    void getPendingRequestsWrong() throws NotValidResourcesException {

        PendingRequestsDTO pendingRequestsDTO = facultyHandlerService.getPendingRequests("FOO");
        assertEquals("Wrong faculty name", pendingRequestsDTO.getStatus());
        assertEquals(new ArrayList<RequestDTO>(), pendingRequestsDTO.getRequests());
    }

    @Test
    void acceptRequestsCorrect() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));

        when(requestRepository.findByRequestId(1L)).thenReturn(request);
        StatusDTO statusDTO = facultyHandlerService.acceptRequests("EEMCS", List.of(1L));
        assertEquals("OK", statusDTO.getStatus());
    }

    @Test
    void acceptRequestsWrong() {
        StatusDTO statusDTO = facultyHandlerService.acceptRequests("FOO", List.of(1L));
        assertEquals("Wrong faculty name", statusDTO.getStatus());
    }

    @Test
    void acceptRequestsHelper() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "desc",
                LocalDate.of(2015, 2, 3), RequestStatus.PENDING);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(1, 1, 1));
        request.setRequestId(1L);

        when(requestRepository.findByRequestId(1L)).thenReturn(request);
        when(requestRepository.findByRequestId(2L)).thenReturn(null);
        List<Long> badRequests = facultyHandlerService.acceptRequestsHelper(FacultyName.EEMCS, List.of(1L, 2L));
        verify(facultyHandler, times(1)).handleAcceptedRequests(FacultyName.EEMCS, request);
        assertEquals(List.of(2L), badRequests);
    }

    @Test
    void acceptRequestsOutputEmpty() {
        StatusDTO statusDTO = facultyHandlerService.acceptRequestsOutput(List.of());
        assertEquals("OK", statusDTO.getStatus());
    }

    @Test
    void acceptRequestsOutputNotEmpty() {
        StatusDTO statusDTO = facultyHandlerService.acceptRequestsOutput(List.of(1L, 2L));
        assertEquals("Could not find the following requests: [1, 2]", statusDTO.getStatus());
    }

    @Test
    void getScheduleForDate() throws NotValidResourcesException {
        Map<FacultyName, List<RequestDTO>> map = new HashMap<>();
        RequestDTO requestDTO = new RequestDTO(1L, "name", "netId", FacultyName.EEMCS,
                "desc", LocalDate.of(2015, 2, 3),
                new Resource(1, 1, 1));
        map.put(FacultyName.EEMCS, List.of(requestDTO));
        LocalDate date = LocalDate.of(2015, 2, 3);
        when(facultyHandler.getRequestForDate(date)).thenReturn(map);
        SysadminScheduleDTO sysadminScheduleDTO = facultyHandlerService.getScheduleForDate(date);
        assertEquals(sysadminScheduleDTO.getSchedule(), map);
    }

    @Test
    void acceptPendingRequestsForTomorrow() {
        facultyHandlerService.acceptPendingRequestsForTomorrow();
        verify(facultyHandler, times(1)).acceptPendingRequestsForTomorrow();
    }
}