package nl.tudelft.sem.resource.manager.domain.providers;

import java.time.LocalDate;

/**
 * An abstract time provider to make services testable.
 * This interface can be mocked in order to provide a predetermined current date and
 * make tests independent of the actual current time.
 */
public interface DateProvider {
    /**
     * Retrieves the current date.
     *
     * @return The current date
     */
    LocalDate getCurrentDate();
}
