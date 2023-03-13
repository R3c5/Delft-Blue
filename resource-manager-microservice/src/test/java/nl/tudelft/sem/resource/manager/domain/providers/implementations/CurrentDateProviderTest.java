package nl.tudelft.sem.resource.manager.domain.providers.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CurrentDateProviderTest {

    private transient CurrentDateProvider sut;

    @BeforeEach
    void setUp() {
        sut = new CurrentDateProvider();
    }

    @Test
    void getCurrentDate() {
        assertThat(sut.getCurrentDate())
                .isEqualTo(LocalDate.now());
    }
}