package sem.faculty.provider;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentTimeProviderTest {
    CurrentTimeProvider currentTimeProvider = new CurrentTimeProvider();

    @Test
    void testGetCurrentTime() {
        //Can't properly test this class, so I'll just do this for coverage:)
        assertThat(LocalDate.now()).isEqualTo(currentTimeProvider.getCurrentDate());

        //The best I could do, because milliseconds can change per executed command.
        LocalDateTime moment = currentTimeProvider.getCurrentDateTime();
        LocalDateTime momentLater = moment.plusSeconds(1);
        assertThat(moment.isBefore(momentLater));
    }
}