package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO("netId", "password", "EMPLOYEE", List.of("EEMCS"));
    }

    @Test
    void notNull() {
        assertNotNull(userDTO);
    }
}