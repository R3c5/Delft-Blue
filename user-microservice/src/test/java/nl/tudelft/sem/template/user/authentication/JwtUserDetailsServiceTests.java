package nl.tudelft.sem.template.user.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import nl.tudelft.sem.template.user.domain.user.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class JwtUserDetailsServiceTests {

    @Autowired
    private transient JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void loadUserByUsername_withValidUser_returnsCorrectUser() {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final HashedPassword testHashedPassword = new HashedPassword("password123Hash");
        final Role testRole = Role.EMPLOYEE;
        final List<FacultyName> testFaculty = new ArrayList<>();
        testFaculty.add(FacultyName.EEMCS);
        testFaculty.add(FacultyName.AE);

        AppUser appUser = new AppUser(testUser, testHashedPassword, testRole, testFaculty);
        userRepository.save(appUser);

        // Act
        UserDetails actual = jwtUserDetailsService.loadUserByUsername(testUser.toString());

        // Assert
        assertThat(actual.getUsername()).isEqualTo(testUser.toString());
        assertThat(actual.getPassword()).isEqualTo(testHashedPassword.toString());
        // Role test not needed because it's for authentication
        // Faculty test not needed because it's for authentication
    }

    @Test
    public void loadUserByUsername_withNonexistentUser_throwsException() {
        // Arrange
        final String testNonexistentUser = "SomeUser";

        final NetId testUser = new NetId("AnotherUser");
        final String testPasswordHash = "password123Hash";
        final Role testRole = Role.EMPLOYEE;
        final List<FacultyName> testFaculty = new ArrayList<>();
        testFaculty.add(FacultyName.EEMCS);

        AppUser appUser = new AppUser(testUser, new HashedPassword(testPasswordHash), testRole, testFaculty);
        userRepository.save(appUser);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> jwtUserDetailsService.loadUserByUsername(testNonexistentUser);

        // Assert
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(action);
    }

    //@AfterAll
    //static void afterAll(@Autowired ApplicationContext applicationContext) {
    //    EmbeddedKafkaBroker embeddedKafkaBroker = applicationContext.getBean(EmbeddedKafkaBroker.class);
    //    embeddedKafkaBroker.destroy();
    //}
}
