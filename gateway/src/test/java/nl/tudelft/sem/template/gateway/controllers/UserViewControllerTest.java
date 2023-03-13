package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import sem.commons.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class UserViewControllerTest {

    private ReplyingKafkaTemplate<String, FacultyNamePackageDTO, RegularUserView> resourceReplyingKafkaTemplateUserView;
    private ReplyingKafkaTemplate<String, DateDTO, SysadminResourceManagerView> replyingKafkaTemplateSysadminResourceView;

    private ReplyingKafkaTemplate<String, DateDTO, SysadminScheduleDTO> replyingKafkaTemplateSysadminScheduleView;
    private AuthManager authManager;
    private UserViewController userViewController;

    private FacultyNameDTO facultyNameDTO;
    private Resource resource;

    private RegularUserView regularUserView;
    private SysadminResourceManagerView sysadminResourceManagerView;
    private SysadminScheduleDTO sysadminScheduleDTO;
    private SysadminUserView sysadminUserView;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        resourceReplyingKafkaTemplateUserView = Mockito.mock(ReplyingKafkaTemplate.class);
        replyingKafkaTemplateSysadminResourceView = Mockito.mock(ReplyingKafkaTemplate.class);
        replyingKafkaTemplateSysadminScheduleView = Mockito.mock(ReplyingKafkaTemplate.class);
        authManager = Mockito.mock(AuthManager.class);
        userViewController = new UserViewController(resourceReplyingKafkaTemplateUserView,
                replyingKafkaTemplateSysadminResourceView, replyingKafkaTemplateSysadminScheduleView, authManager);

        facultyNameDTO = new FacultyNameDTO("EEMCS");
        resource = new Resource(3, 2, 1);
        regularUserView = new RegularUserView(Map.of(facultyNameDTO, resource));
        sysadminResourceManagerView = new SysadminResourceManagerView(new HashMap<>(), new Resource(3, 2, 1),
                new ArrayList<>());
        sysadminScheduleDTO = new SysadminScheduleDTO(new HashMap<>());
        sysadminUserView = new SysadminUserView(sysadminResourceManagerView, sysadminScheduleDTO);
    }

    @Test
    void getNormalUserView() throws ExecutionException, InterruptedException, TimeoutException {
        RequestReplyFuture<String, FacultyNamePackageDTO, RegularUserView> future =
                Mockito.mock(RequestReplyFuture.class);
        ConsumerRecord<String, RegularUserView> consumerRecord =
                new ConsumerRecord<>("test", 1, 1L, "test",
                        regularUserView);
        Mockito.when(future.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        Mockito.when(authManager.getFaculties()).thenReturn("[EEMCS, AE]");
        Mockito.when(resourceReplyingKafkaTemplateUserView.sendAndReceive(Mockito.any())).thenReturn(future);

        ResponseEntity<RegularUserView> responseEntity = userViewController.getNormalUserView();
        assertThat(responseEntity.getBody()).isEqualTo(regularUserView);
    }

    @Test
    void getSysAdminUserView() throws ExecutionException, InterruptedException, TimeoutException {
        RequestReplyFuture<String, DateDTO, SysadminScheduleDTO> futureSchedule =
                Mockito.mock(RequestReplyFuture.class);
        RequestReplyFuture<String, DateDTO, SysadminResourceManagerView> futureResources =
                Mockito.mock(RequestReplyFuture.class);
        ConsumerRecord<String, SysadminResourceManagerView> consumerRecordResources =
                new ConsumerRecord<>("test", 1, 1L, "test",
                        sysadminResourceManagerView);
        ConsumerRecord<String, SysadminScheduleDTO> consumerRecordFaculty =
                new ConsumerRecord<>("test", 1, 1L, "test",
                        sysadminScheduleDTO);
        Mockito.when(futureResources.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecordResources);
        Mockito.when(futureSchedule.get(10, TimeUnit.SECONDS)).thenReturn(consumerRecordFaculty);
        Mockito.when(replyingKafkaTemplateSysadminResourceView.sendAndReceive(Mockito.any()))
                .thenReturn(futureResources);
        Mockito.when(replyingKafkaTemplateSysadminScheduleView.sendAndReceive(Mockito.any()))
                .thenReturn(futureSchedule);

        ResponseEntity<SysadminUserView> responseEntity = userViewController.getSysadminView(new DateDTO(1, 1, 1));
        assertThat(responseEntity.getBody()).isEqualTo(sysadminUserView);
    }
}