package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class URLTest {

    String name;
    URL url;

    @BeforeEach
    void setUp() {
        name = "test";
        url = new URL(name);
    }

    @Test
    void getName() {
        assertThat(url.getUrlValue()).isEqualTo(name);
    }

    @Test
    void testEquals() {
        assertThat(new URL("test")).isEqualTo(url);
    }

    @Test
    void testHashCode() {
        assertThat(new URL("test").hashCode()).isEqualTo(url.hashCode());
    }
}