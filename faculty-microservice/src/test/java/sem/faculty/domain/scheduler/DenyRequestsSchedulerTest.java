package sem.faculty.domain.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sem.commons.FacultyName;
import sem.commons.Resource;
import sem.commons.NotValidResourcesException;
import sem.faculty.domain.*;
import sem.faculty.provider.CurrentTimeProvider;

import java.time.LocalDate;
import java.time.Month;

class DenyRequestsSchedulerTest {

    DenyRequestsScheduler denyScheduler;
    @Mock
    private final RequestRepository requestRepository = mock(RequestRepository.class);

    @BeforeEach
    void setup() {
        denyScheduler = new DenyRequestsScheduler(requestRepository);
    }

    @Test
    void testScheduleRequest() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("name", "description",
                LocalDate.of(2022, Month.DECEMBER, 15), RequestStatus.DROPPED);
        Request request = new Request(rd, "netId", FacultyName.EEMCS, new Resource(5, 1, 1));

        denyScheduler.scheduleRequest(request,
                new Faculty(FacultyName.ARCH, new CurrentTimeProvider()));

        assertThat(request.getStatus()).isEqualTo(RequestStatus.DENIED);
    }
}
