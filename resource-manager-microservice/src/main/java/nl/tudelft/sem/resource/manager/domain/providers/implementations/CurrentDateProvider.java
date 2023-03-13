package nl.tudelft.sem.resource.manager.domain.providers.implementations;

import nl.tudelft.sem.resource.manager.domain.providers.DateProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * An abstract time provider to make services testable.
 * The CurrentDateProvider interface can be mocked in order to provide a predetermined current time and
 * make tests independent of the actual current time.
 */
@Component
public class CurrentDateProvider implements DateProvider {
    /**
     * Gets current time.
     *
     * @return The current time
     */
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
