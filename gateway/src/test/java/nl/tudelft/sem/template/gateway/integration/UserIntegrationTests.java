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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "mockKafkaTemplates"})
@AutoConfigureMockMvc
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient AuthManager mockAuthenticationManager;

    @Autowired
    private transient ReplyingKafkaTemplate<String, UserDTO, StatusDTO> replyingKafkaTemplateNewUser;

    @Autowired
    private transient ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> replyingKafkaTemplateUserAuth;

    @Test
    public void addNewUserTest() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_EMPLOYEE");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        UserDTO user = new UserDTO("netId", "pass", "EMPLOYEE", List.of("EEMCS"));
        ConsumerRecord<String, StatusDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key", new StatusDTO("OK"));

        RequestReplyFuture<String, UserDTO, StatusDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateNewUser.sendAndReceive(any())).thenReturn(future);

        ResultActions resultActions = mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(user))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        String responseResult = result.getResponse().getContentAsString();

        assertThat(responseResult).isEqualTo("Added new user");
    }

    @Test
    public void authenticateUserTest() throws Exception {

        when(mockAuthenticationManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthenticationManager.getFaculties()).thenReturn("[EEMCS]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRole(anyString())).thenReturn("ROLE_EMPLOYEE");
        when(mockJwtTokenVerifier.getFaculties(anyString())).thenReturn("EEMCS");

        UserCredentials userCredentials = new UserCredentials("netId", "pass");
        ConsumerRecord<String, TokenDTO> consumerRecord =
                new ConsumerRecord<>("topic", 1, 1L, "key",
                        new TokenDTO("OK", "someToken"));

        RequestReplyFuture<String, UserCredentials, TokenDTO> future = Mockito.mock(RequestReplyFuture.class);

        when(future.get()).thenReturn(consumerRecord);
        when(replyingKafkaTemplateUserAuth.sendAndReceive(any())).thenReturn(future);

        ResultActions resultActions = mockMvc.perform(post("/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(userCredentials))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andReturn();

        TokenDTO responseResult = JsonUtil.deserialize(result.getResponse().getContentAsString(),
                TokenDTO.class);

        assertThat(responseResult.getStatus()).isEqualTo("OK");
        assertThat(responseResult.getToken()).isEqualTo("someToken");
    }
}
