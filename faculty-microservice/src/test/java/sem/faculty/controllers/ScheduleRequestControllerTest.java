package sem.faculty.controllers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import sem.commons.*;
import sem.faculty.domain.Faculty;
import sem.faculty.provider.CurrentTimeProvider;
import sem.faculty.provider.TimeProvider;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleRequestControllerTest {

    @Mock
    private final TimeProvider timeProvider = mock(CurrentTimeProvider.class);

    @Mock
    private ReplyingKafkaTemplate<String, ScheduleDateDTO, LocalDate> replyingKafkaScheduleRequest;

    @Mock
    private ReplyingKafkaTemplate<String, ScheduleDateDTO, StatusDTO> replyingKafkaReserveResources;

    @Mock
    private RequestReplyFuture<String, ScheduleDateDTO, LocalDate> sendAndReceive;

    @Mock
    ConsumerRecord<String, LocalDate> consumerRecord;

    ScheduleRequestController controller;

    @BeforeEach
    void setUp() {
        replyingKafkaScheduleRequest = Mockito.mock(ReplyingKafkaTemplate.class);
        replyingKafkaReserveResources = Mockito.mock(ReplyingKafkaTemplate.class);
        controller = new ScheduleRequestController(replyingKafkaScheduleRequest, replyingKafkaReserveResources);
        sendAndReceive = Mockito.mock(RequestReplyFuture.class);
        consumerRecord = Mockito.mock(ConsumerRecord.class);
    }

    @Test
    void sendScheduleRequest()
            throws NotValidResourcesException, ExecutionException, InterruptedException, TimeoutException {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 17);
        Faculty faculty = new Faculty(FacultyName.ARCH, timeProvider);
        ScheduleDateDTO dateDTO =
                new ScheduleDateDTO(new Resource(5, 1, 1), date, faculty.getFacultyName());

        when(replyingKafkaScheduleRequest.sendAndReceive(any())).thenReturn(sendAndReceive);
        when(sendAndReceive.get(20, TimeUnit.SECONDS)).thenReturn(consumerRecord);

        assertThat(controller.sendScheduleRequest(dateDTO).equals(ResponseEntity.ok(date)));

        ProducerRecord<String, ScheduleDateDTO> record =
                new ProducerRecord<>("schedule-date", dateDTO);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "schedule-date-reply".getBytes()));
        verify(replyingKafkaScheduleRequest, times(1)).sendAndReceive(record);
    }
}