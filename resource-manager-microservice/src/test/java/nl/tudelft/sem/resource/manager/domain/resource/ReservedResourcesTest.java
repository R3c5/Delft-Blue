package nl.tudelft.sem.resource.manager.domain.resource;

import nl.tudelft.sem.resource.manager.domain.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReservedResourcesTest {

    private transient ReservedResources rr1;
    private transient ReservedResources rr2;

    @BeforeEach
    void setUp() {
        rr1 = new ReservedResources(
                new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AS),
                Resource.with(100)
        );
        rr2 = new ReservedResources(
                new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AS),
                Resource.with(100)
        );
    }

    @Test
    void equals_test() {
        assertThat(rr1).isEqualTo(rr2);
    }

    @Test
    void not_equals_test1() {
        rr1.setResources(Resource.with(200));
        assertThat(rr1).isNotEqualTo(rr2);
    }

    @Test
    void not_equals_test2() {
        rr1.setId(new ReservedResourceId(LocalDate.of(2022, 1, 2), Reserver.AS));
        assertThat(rr1).isNotEqualTo(rr2);
    }

    @Test
    void not_equals_test3() {
        rr1.setId(new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AE));
        assertThat(rr1).isNotEqualTo(rr2);
    }

    @Test
    void not_equals_different_type_test() {
        assertThat(rr1).isNotEqualTo(new String("a"));
    }

    @Test
    void hash_code_test() {
        assertThat(rr1.hashCode()).isEqualTo(rr2.hashCode());
    }

    @Test
    void hash_code_not_equal_test1() {
        rr1.setResources(Resource.with(200));
        assertThat(rr1.hashCode()).isNotEqualTo(rr2.hashCode());
    }

    @Test
    void hash_code_not_equal_test2() {
        rr1.setId(new ReservedResourceId(LocalDate.of(2022, 1, 2), Reserver.AS));
        assertThat(rr1.hashCode()).isNotEqualTo(rr2.hashCode());
    }

    @Test
    void hash_code_not_equal_test3() {
        rr1.setId(new ReservedResourceId(LocalDate.of(2022, 1, 1), Reserver.AE));
        assertThat(rr1.hashCode()).isNotEqualTo(rr2.hashCode());
    }
}