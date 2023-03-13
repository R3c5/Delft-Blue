package nl.tudelft.sem.resource.manager.domain.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReservedResourceIdTest {

    private transient ReservedResourceId id1;
    private transient ReservedResourceId id2;

    @BeforeEach
    void setUp() {
        id1 = new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AS);
        id2 = new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AS);
    }

    @Test
    void equals_test() {
        assertThat(id1).isEqualTo(id2);
    }

    @Test
    void not_equals_test() {
        assertThat(id1).isNotEqualTo(new String("a"));
        id1 = new ReservedResourceId(LocalDate.of(2022, 1, 2), Reserver.AS);
        assertThat(id1).isNotEqualTo(id2);
        id1 = new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AE);
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void hash_code_test() {
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void hash_code_fail_test() {
        id1 = new ReservedResourceId(LocalDate.of(2022, 1, 2), Reserver.AS);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
        id1 = new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AE);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
}