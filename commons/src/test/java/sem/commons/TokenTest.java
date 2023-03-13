package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    String name;
    Token token;

    @BeforeEach
    void setUp() {
        name = "test";
        token = new Token(name);
    }

    @Test
    void getName() {
        assertThat(token.getTokenValue()).isEqualTo(name);
    }

    @Test
    void testEquals() {
        assertThat(new Token("test")).isEqualTo(token);
    }

    @Test
    void testHashCode() {
        assertThat(new Token("test").hashCode()).isEqualTo(token.hashCode());
    }
}