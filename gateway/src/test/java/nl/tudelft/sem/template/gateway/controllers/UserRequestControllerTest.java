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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserRequestControllerTest {

    private UserRequestController userRequestController;
    private RequestDTO requestDTO;

    @Mock
    private transient ReplyingKafkaTemplate<String, RequestDTO, StatusDTO> replyingKafkaTemplate;

    @Mock
    private transient AuthManager authManager;

    @BeforeEach
    void setup() throws NotValidResourcesException {
        requestDTO = new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1));
        replyingKafkaTemplate = Mockito.mock(ReplyingKafkaTemplate.class);
        authManager = Mockito.mock(AuthManager.class);
        userRequestController = new UserRequestController(authManager, replyingKafkaTemplate);
    }

    @Test
    void sendUserRequestTest() throws ExecutionException, InterruptedException {

        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("OK"));
        RequestReplyFuture<String, RequestDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(authManager.getNetId()).thenReturn("netId");
        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplate.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<StatusDTO> response = userRequestController.sendUserRequest(requestDTO);

        assertEquals(response.getStatusCodeValue(), 200);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), "Request was sent");
    }

    @Test
    void sendUserRequestTestWrongFaculty() {


        when(authManager.getFaculties()).thenReturn("[CEG]");
        when(authManager.getNetId()).thenReturn("netId");

        ResponseEntity<StatusDTO> response = userRequestController.sendUserRequest(requestDTO);

        assertEquals(response.getStatusCodeValue(), 401);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(),
                "You can only make requests to your own faculty!");
    }

    @Test
    void sendUserRequestTestWrongNetId() {

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(authManager.getNetId()).thenReturn("somethingWrong");

        ResponseEntity<StatusDTO> response = userRequestController.sendUserRequest(requestDTO);

        assertEquals(response.getStatusCodeValue(), 401);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(),
                "You cannot make requests for someone else!");
    }

    @Test
    void sendUserRequestTestWrongResponse() throws ExecutionException, InterruptedException {

        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("Wrong"));
        RequestReplyFuture<String, RequestDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(authManager.getFaculties()).thenReturn("[EEMCS]");
        when(authManager.getNetId()).thenReturn("netId");
        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplate.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<StatusDTO> response = userRequestController.sendUserRequest(requestDTO);

        assertEquals(response.getStatusCodeValue(), 400);
        assertEquals(Objects.requireNonNull(response.getBody()).getStatus(), "Wrong");
    }
}
