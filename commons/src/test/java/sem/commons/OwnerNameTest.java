package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class OwnerNameTest {

    String name;
    OwnerName ownerName;

    @BeforeEach
    void setUp() {
        name = "test";
        ownerName = new OwnerName(name);
    }

    @Test
    void getName() {
        assertThat(ownerName.getName()).isEqualTo(name);
    }

    @Test
    void testEquals() {
        assertThat(new OwnerName("test")).isEqualTo(ownerName);
    }

    @Test
    void testHashCode() {
        assertThat(new OwnerName("test").hashCode()).isEqualTo(ownerName.hashCode());
    }
}