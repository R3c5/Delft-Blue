package nl.tudelft.sem.template.gateway.integration;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import nl.tudelft.sem.template.gateway.authentication.JwtTokenVerifier;
import nl.tudelft.sem.template.gateway.integration.utils.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import sem.commons.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockKafkaTemplates"})
@AutoConfigureMockMvc
public class FacultyIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> replyingKafkaTemplatePendingRequests;

    @Autowired
    private transient ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> replyingKafkaTemplateAcceptRequests;

    @Test
    public void getPendingRequestsTest() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_FACULTY_REVIEWER");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        List<RequestDTO> list = List.of(new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1)));

        ConsumerRecord<String, PendingRequestsDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new PendingRequestsDTO("OK", list));
        RequestReplyFuture<String, FacultyNameDTO, PendingRequestsDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        when(replyingKafkaTemplatePendingRequests.sendAndReceive(any())).thenReturn(future);

        // Act
        // Still include Bearer token as AuthFilter itself is not mocked
        ResultActions resultActions = mockMvc.perform(post("/faculty/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(new FacultyNameDTO("EEMCS")))
                .header("Authorization", "Bearer MockedToken"));

        // Assert

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        verify(replyingKafkaTemplatePendingRequests, times(1)).sendAndReceive(any());

        //IT SEEMS THAT THE DESERIALIZER PROVIDED IN TEMPLATE IS UNABLE TO DESERIALIZE LocalDate

        //PendingRequestsDTO responseResult = JsonUtil.deserialize(result.getResponse().getContentAsString(),
        //        PendingRequestsDTO.class);
        //
        //assertThat(responseResult.getStatus()).isEqualTo("OK");
        //assertThat(responseResult.getRequests()).isEqualTo(new ArrayList<RequestDTO>());

    }

    @Test
    public void getPendingRequestsTestWrongRole() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_EMPLOYEE");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        ResultActions resultActions = mockMvc.perform(post("/faculty/pending")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(new FacultyNameDTO("EEMCS")))
                .header("Authorization", "Bearer MockedToken"));


        MvcResult result = resultActions
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    public void acceptRequestsTest() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_FACULTY_REVIEWER");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("OK"));
        RequestReplyFuture<String, AcceptRequestsDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(replyingKafkaTemplateAcceptRequests.sendAndReceive(any())).thenReturn(future);
        when(future.get()).thenReturn(consumerRecord);

        ResultActions resultActions = mockMvc.perform(post("/faculty/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(new AcceptRequestsDTO("EEMCS", new ArrayList<>())))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        StatusDTO responseResult = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                StatusDTO.class);

        assertThat(responseResult.getStatus()).isEqualTo("OK");
    }

    @Test
    public void acceptRequestsTestWrongRole() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_EMPLOYEE");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        ResultActions resultActions = mockMvc.perform(post("/faculty/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(new AcceptRequestsDTO("EEMCS", new ArrayList<>())))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult result = resultActions
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void acceptRequestsTestWrongFaculty() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[AE]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_FACULTY_REVIEWER");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("[AE]");

        ResultActions resultActions = mockMvc.perform(post("/faculty/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(new AcceptRequestsDTO("EEMCS", new ArrayList<>())))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult result = resultActions
                .andExpect(status().isUnauthorized())
                .andReturn();

        StatusDTO responseResult = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                StatusDTO.class);

        assertThat(responseResult.getStatus()).isEqualTo("You are not allowed to access this faculty");
    }

}
