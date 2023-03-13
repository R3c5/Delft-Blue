package nl.tudelft.sem.template.gateway.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthManagerTest {

    private transient AuthManager authManager;

    @BeforeEach
    void setUp() {
        authManager = new AuthManager();
    }

    @Test
    void getNetId() {
        String expected = "testUser";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, List.of() // no credentials and no authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String actual = authManager.getNetId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFaculties() {
        String expected = "EEMCS";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                "testUser",
                null, List.of(
                    new SimpleGrantedAuthority("ROLE_EMPLOYEE"),
                    new SimpleGrantedAuthority("EEMCS")
        )
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String actual = authManager.getFaculties();

        assertThat(actual).isEqualTo(expected);
    }
}