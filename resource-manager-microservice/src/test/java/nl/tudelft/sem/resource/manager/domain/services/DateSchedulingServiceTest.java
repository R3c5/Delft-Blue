package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.providers.implementations.CurrentDateProvider;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DateSchedulingServiceTest {
    private transient DateSchedulingService sut;
    private transient CurrentDateProvider currentDateProvider;
    private transient ResourceAvailabilityService resourceAvailabilityService;

    @BeforeEach
    void setUp() {
        currentDateProvider = mock(CurrentDateProvider.class);
        resourceAvailabilityService = mock(ResourceAvailabilityService.class);
        sut = new DateSchedulingService(currentDateProvider, resourceAvailabilityService);
    }

    @Test
    void get_date_for_request_test() {
        LocalDate today = LocalDate.of(2022, 1, 1);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate afterTomorrow = today.plusDays(2);
        Reserver faculty = Reserver.AE;
        when(currentDateProvider.getCurrentDate()).thenReturn(today);
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(today, faculty))
                .thenReturn(Resource.with(150));
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(tomorrow, faculty))
                .thenReturn(Resource.with(100));
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(afterTomorrow, faculty))
                .thenReturn(Resource.with(50));
        Resource neededResources = Resource.with(70);

        LocalDate chosenDate = sut.getDateForRequest(neededResources, afterTomorrow, faculty);

        assertThat(chosenDate).isEqualTo(tomorrow);
    }

    @Test
    void no_available_date_for_request_test() {
        LocalDate today = LocalDate.of(2022, 1, 1);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate afterTomorrow = today.plusDays(2);
        Reserver faculty = Reserver.AE;
        when(currentDateProvider.getCurrentDate()).thenReturn(today);
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(today, faculty))
                .thenReturn(Resource.with(150));
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(tomorrow, faculty))
                .thenReturn(Resource.with(100));
        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(afterTomorrow, faculty))
                .thenReturn(Resource.with(50));
        Resource neededResources = Resource.with(160);

        LocalDate chosenDate = sut.getDateForRequest(neededResources, afterTomorrow, faculty);

        assertThat(chosenDate).isNull();
    }
}