package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import sem.commons.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FacultyControllerTest {

    @Mock
    private AuthManager authManager;

    @Mock
    private ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> replyingKafkaTemplatePendingRequestsMock;

    @Mock
    private ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> replyingKafkaTemplateAcceptRequestsMock;

    private FacultyController facultyController;

    @BeforeEach
    void setUp() {
        authManager = Mockito.mock(AuthManager.class);
        replyingKafkaTemplatePendingRequestsMock = Mockito.mock(ReplyingKafkaTemplate.class);
        replyingKafkaTemplateAcceptRequestsMock = Mockito.mock(ReplyingKafkaTemplate.class);

        facultyController = new FacultyController(
                authManager,
                replyingKafkaTemplatePendingRequestsMock,
                replyingKafkaTemplateAcceptRequestsMock
        );
    }

    @Test
    void getPendingRequests() throws NotValidResourcesException, ExecutionException, InterruptedException, TimeoutException {

        List<RequestDTO> list = List.of(new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1)));

        ConsumerRecord<String, PendingRequestsDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new PendingRequestsDTO("OK", list));
        RequestReplyFuture<String, FacultyNameDTO, PendingRequestsDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(future.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        when(replyingKafkaTemplatePendingRequestsMock.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<PendingRequestsDTO> response = facultyController.getPendingRequests(new FacultyNameDTO("EEMCS"));

        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(response.getBody()).getRequests(), list);
    }

    @Test
    void getPendingRequestsWrongCode() throws ExecutionException, InterruptedException, TimeoutException {

        List<RequestDTO> list = new ArrayList<>();

        ConsumerRecord<String, PendingRequestsDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new PendingRequestsDTO("WRONG", list));
        RequestReplyFuture<String, FacultyNameDTO, PendingRequestsDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(future.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        when(replyingKafkaTemplatePendingRequestsMock.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<PendingRequestsDTO> response = facultyController.getPendingRequests(new FacultyNameDTO("EEMCS"));

        assertEquals(response.getStatusCodeValue(), 400);
        assertEquals(Objects.requireNonNull(response.getBody()).getRequests(), list);
    }

    @Test
    void getPendingRequestsWrongFaculty() throws ExecutionException, InterruptedException, TimeoutException {

        when(authManager.getFaculties()).thenReturn("[AE]");

        ResponseEntity<PendingRequestsDTO> response = facultyController.getPendingRequests(new FacultyNameDTO("EEMCS"));

        assertEquals(response.getStatusCodeValue(), 401);
        assertEquals("You are not allowed to access this faculty", response.getBody().getStatus());
    }

    @Test
    void acceptRequests() throws ExecutionException, InterruptedException {
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("OK"));
        RequestReplyFuture<String, AcceptRequestsDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(replyingKafkaTemplateAcceptRequestsMock.sendAndReceive(any())).thenReturn(future);
        when(future.get()).thenReturn(consumerRecord);

        ResponseEntity<StatusDTO> response = facultyController.acceptRequests(
                new AcceptRequestsDTO("EEMCS", new ArrayList<>()));

        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), "OK");
    }

    @Test
    void acceptRequestsWrongStatus() throws ExecutionException, InterruptedException {
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("WRONG"));
        RequestReplyFuture<String, AcceptRequestsDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(replyingKafkaTemplateAcceptRequestsMock.sendAndReceive(any())).thenReturn(future);
        when(future.get()).thenReturn(consumerRecord);

        ResponseEntity<StatusDTO> response = facultyController.acceptRequests(
                new AcceptRequestsDTO("EEMCS", new ArrayList<>()));

        assertEquals(response.getStatusCodeValue(), 400);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), "WRONG");
    }

    @Test
    void acceptRequestsWrongFaculty() throws ExecutionException, InterruptedException {
        when(authManager.getFaculties()).thenReturn("[EEMCS]");

        ResponseEntity<StatusDTO> response = facultyController.acceptRequests(
                new AcceptRequestsDTO("AE", new ArrayList<>()));

        assertEquals(response.getStatusCodeValue(), 401);
        assertEquals("You are not allowed to access this faculty", response.getBody().getStatus());
    }
}