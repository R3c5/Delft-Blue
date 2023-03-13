package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenDTOTest {

    TokenDTO tokenDTO;

    @BeforeEach
    void setUp() {
        tokenDTO = new TokenDTO("OK", "token");
    }

    @Test
    void notNull() {
        assertNotNull(tokenDTO);
    }
}