package sem.faculty.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectRequestExceptionTest {

    @Test
    void constructorNotNull() {
        Exception exception = new RejectRequestException(1L);

        assertNotNull(exception);
        assertEquals("1", exception.getMessage());
    }

}