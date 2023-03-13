package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import sem.commons.StatusDTO;
import sem.commons.TokenDTO;
import sem.commons.UserCredentials;
import sem.commons.UserDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private AuthManager authManager;
    private ReplyingKafkaTemplate<String, UserDTO, StatusDTO> replyingKafkaTemplateNewUser;
    private ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> replyingKafkaTemplateUserAuth;

    private UserController userController;

    @BeforeEach
    void setUp() {
        authManager = Mockito.mock(AuthManager.class);
        replyingKafkaTemplateNewUser = Mockito.mock(ReplyingKafkaTemplate.class);
        replyingKafkaTemplateUserAuth = Mockito.mock(ReplyingKafkaTemplate.class);

        userController = new UserController(
                authManager,
                replyingKafkaTemplateNewUser,
                replyingKafkaTemplateUserAuth
        );
    }

    @Test
    void addNewUser() throws ExecutionException, InterruptedException {
        UserDTO user = new UserDTO("netId", "pass", "EMPLOYEE", List.of("EEMCS"));
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("OK"));

        RequestReplyFuture<String, UserDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateNewUser.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<String> response = userController.addNewUser(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Added new user", response.getBody());
    }

    @Test
    void addNewUserWrongFaculty() throws ExecutionException, InterruptedException {
        UserDTO user = new UserDTO("netId", "pass", "EMPLOYEE", List.of("OO"));
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key",
                        new StatusDTO("Wrong faculty name"));

        RequestReplyFuture<String, UserDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateNewUser.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<String> response = userController.addNewUser(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Wrong faculty name", response.getBody());
    }

    @Test
    void addNewUserWrongRole() throws ExecutionException, InterruptedException {
        UserDTO user = new UserDTO("netId", "pass", "EMPLOYEE", List.of("OO"));
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key",
                        new StatusDTO("Provided role does not exist!"));

        RequestReplyFuture<String, UserDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateNewUser.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<String> response = userController.addNewUser(user);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Provided role does not exist!", response.getBody());
    }

    @Test
    void authenticateUser() throws ExecutionException, InterruptedException {
        UserCredentials userCredentials = new UserCredentials("netId", "pass");
        ConsumerRecord<String, TokenDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key",
                        new TokenDTO("OK", "someToken"));

        RequestReplyFuture<String, UserCredentials, TokenDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateUserAuth.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<TokenDTO> response = userController.authenticateUser(userCredentials);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("OK", response.getBody().getStatus());
    }

    @Test
    void authenticateUserWrongCredentials() throws ExecutionException, InterruptedException {
        UserCredentials userCredentials = new UserCredentials("netId", "pass");
        ConsumerRecord<String, TokenDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key",
                        new TokenDTO("NVALID_CREDENTIALS", ""));

        RequestReplyFuture<String, UserCredentials, TokenDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateUserAuth.sendAndReceive(any())).thenReturn(future);

        ResponseEntity<TokenDTO> response = userController.authenticateUser(userCredentials);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("NVALID_CREDENTIALS", response.getBody().getStatus());
    }

    @Test
    void helloWorld() {
        when(authManager.getNetId()).thenReturn("userNetId");
        when(authManager.getFaculties()).thenReturn("EEMCS");

        ResponseEntity<String> response = userController.helloWorld();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hello userNetId from faculty EEMCS", response.getBody());
    }
}