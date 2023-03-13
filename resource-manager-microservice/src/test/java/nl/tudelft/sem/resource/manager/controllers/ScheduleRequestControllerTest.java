package nl.tudelft.sem.resource.manager.controllers;

import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import nl.tudelft.sem.resource.manager.domain.services.Manager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sem.commons.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleRequestControllerTest {

    private transient ScheduleRequestController sut;
    @Mock private transient Manager manager;

    @BeforeEach
    void setUp() {
        sut = new ScheduleRequestController(manager);
    }

    @Test
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    void get_available_date_for_request_test() throws NotValidResourcesException {
        ScheduleDateDTO dto = new ScheduleDateDTO(
                new Resource(10, 10, 10),
                LocalDate.of(2022, 1, 3),
                FacultyName.AE
        );

        when(manager.getDateForRequest(
                nl.tudelft.sem.resource.manager.domain.Resource.with(10),
                LocalDate.of(2022, 1, 3),
                Reserver.AE)
        ).thenReturn(LocalDate.of(2022, 1, 2));

        assertThat(sut.getAvailableDateForRequest(
                new ConsumerRecord<>("test", 1, 1, "test", dto),
                dto
        )).isEqualTo(LocalDate.of(2022, 1, 2));
    }

    @Test
    void reserve_resources_for_request_test() throws NotValidResourcesException, NotEnoughResourcesException {
        ScheduleDateDTO dto = new ScheduleDateDTO(
                new Resource(10, 10, 10),
                LocalDate.of(2022, 1, 3),
                FacultyName.AE
        );

        assertThat(sut.reserveResourcesForRequest(
                new ConsumerRecord<>("test", 1, 1, "test", dto),
                dto
        )).isEqualTo(new StatusDTO("OK"));

        ArgumentCaptor<nl.tudelft.sem.resource.manager.domain.Resource> resourceArg =
                ArgumentCaptor.forClass(nl.tudelft.sem.resource.manager.domain.Resource.class);
        ArgumentCaptor<Reserver> reserverArg = ArgumentCaptor.forClass(Reserver.class);
        ArgumentCaptor<LocalDate> dateArg = ArgumentCaptor.forClass(LocalDate.class);

        verify(manager).reserveResourcesOnDay(reserverArg.capture(), resourceArg.capture(), dateArg.capture());

        assertThat(resourceArg.getValue()).isEqualTo(nl.tudelft.sem.resource.manager.domain.Resource.with(10));
        assertThat(reserverArg.getValue()).isEqualTo(Reserver.AE);
        assertThat(dateArg.getValue()).isEqualTo(LocalDate.of(2022, 1, 3));
    }

    @Test
    void reserve_resources_for_request_fails_test() throws NotValidResourcesException, NotEnoughResourcesException {
        ScheduleDateDTO dto = new ScheduleDateDTO(
                new Resource(10, 10, 10),
                LocalDate.of(2022, 1, 3),
                FacultyName.AE
        );

        doThrow(NotEnoughResourcesException.class)
            .when(manager)
            .reserveResourcesOnDay(
                    Reserver.AE,
                    nl.tudelft.sem.resource.manager.domain.Resource.with(10),
                    LocalDate.of(2022, 1, 3)
            );


        assertThat(
            sut.reserveResourcesForRequest(
                new ConsumerRecord<>("test", 1, 1, "test", dto),
                dto
            )
        ).isNotEqualTo(new StatusDTO("OK"));
    }
}