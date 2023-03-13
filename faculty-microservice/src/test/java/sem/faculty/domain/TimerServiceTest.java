package sem.faculty.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import sem.faculty.handler.FacultyHandlerService;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class TimerServiceTest {

    @Mock
    private FacultyHandlerService facultyHandlerService;

    private TimerService timerService;

    @BeforeEach
    void setup() {
        facultyHandlerService = Mockito.mock(FacultyHandlerService.class);
        timerService = new TimerService(facultyHandlerService);
    }

    @Test
    void constructorNotNull() {
        assertNotNull(timerService);
    }

    @Test
    void acceptPendingRequestsForTomorrow() {
        timerService.acceptPendingRequestsForTomorrow();
        verify(facultyHandlerService).acceptPendingRequestsForTomorrow();
    }
}