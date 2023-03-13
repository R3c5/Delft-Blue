package nl.tudelft.sem.template.gateway.profiles;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import sem.commons.*;

@Profile("mockKafkaTemplates")
@Configuration
public class MockKafkaTemplatesProfile {

    @Bean
    @Primary
    public ReplyingKafkaTemplate<String, FacultyNameDTO, PendingRequestsDTO> getKafkaTemplateFN_PR() {
        return Mockito.mock(ReplyingKafkaTemplate.class);
    }

    @Bean
    @Primary
    public ReplyingKafkaTemplate<String, AcceptRequestsDTO, StatusDTO> getKafkaTemplateAR_S() {
        return Mockito.mock(ReplyingKafkaTemplate.class);
    }

    @Bean
    @Primary
    public ReplyingKafkaTemplate<String, UserDTO, StatusDTO> getKafkaTemplateU_S() {
        return Mockito.mock(ReplyingKafkaTemplate.class);
    }

    @Bean
    @Primary
    public ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> getKafkaTemplateUC_T() {
        return Mockito.mock(ReplyingKafkaTemplate.class);
    }
}
