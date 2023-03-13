package nl.tudelft.sem.resource.manager.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {

    private transient Resource resource1;
    private transient Resource resource2;

    @BeforeEach
    void setUp() {
        resource1 = Resource.with(10);
        resource2 = Resource.with(10);
    }

    @Test
    void equals_test() {
        assertThat(resource1).isEqualTo(resource2);
    }

    @Test
    void not_equals_test() {
        resource1.setMemResources(20);
        assertThat(resource1).isNotEqualTo(resource2);
    }

    @Test
    void not_equals_different_type_test() {
        assertThat(resource1).isNotEqualTo(new String("a"));
    }

    @Test
    void hash_code_test() {
        assertThat(resource1.hashCode()).isEqualTo(resource2.hashCode());
    }

    @Test
    void hash_code_not_equal_test() {
        resource1.setCpuResources(20);
        assertThat(resource1.hashCode()).isNotEqualTo(resource2.hashCode());
    }
}