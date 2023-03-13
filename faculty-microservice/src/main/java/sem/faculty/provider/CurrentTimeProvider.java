package sem.faculty.provider;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Implementation of TimeProvider interface that returns the current date.
 * This is done to allow easy mocking.
 */
@Component
public class CurrentTimeProvider implements TimeProvider {

    public CurrentTimeProvider() {}

    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
