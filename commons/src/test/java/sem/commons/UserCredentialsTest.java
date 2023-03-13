package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialsTest {

    UserCredentials uc;

    @BeforeEach
    void setup() {
        uc = new UserCredentials("test", "password");
    }

    @Test
    void notNullConstructor() {
        assertNotNull(uc);
    }

    @Test
    void getNetId() {
        assertEquals("test", uc.getNetId());
    }

    @Test
    void getPassword() {
        assertEquals("password", uc.getPassword());
    }

    @Test
    void setNetId() {
        uc.setNetId("newNetId");
        assertEquals("newNetId", uc.getNetId());
    }

    @Test
    void setPassword() {
        uc.setPassword("newPassword");
        assertEquals("newPassword", uc.getPassword());
    }

    @Test
    void testEquals() {
        UserCredentials uc2 = new UserCredentials("test", "password");
        assertEquals(uc, uc2);
    }

    @Test
    void canEqual() {
        UserCredentials uc2 = new UserCredentials("test", "password");
        assertTrue(uc.canEqual(uc2));
    }

    @Test
    void testToString() {
        assertEquals("UserCredentials(netId=test, password=password)", uc.toString());
    }
}