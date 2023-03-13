package sem.faculty.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotValidResourcesExceptionTest {

    @Test
    void constructorNotNull() {
        Exception exception = new NotValidResourcesException("message");
        assertNotNull(exception);
        assertEquals("message", exception.getMessage());
    }

}