package sem.faculty.provider;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TimeProvider {
    /**
     * Retrieves the current time.
     *
     * @return The current time
     */
    LocalDate getCurrentDate();

    LocalDateTime getCurrentDateTime();
}
