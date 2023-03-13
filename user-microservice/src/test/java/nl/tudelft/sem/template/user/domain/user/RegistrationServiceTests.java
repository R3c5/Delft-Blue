package nl.tudelft.sem.template.user.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class RegistrationServiceTests {

    @Autowired
    private transient RegistrationService registrationService;

    @Autowired
    private transient PasswordHashingService mockPasswordEncoder;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void createUser_withValidData_worksCorrectly() throws Exception {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final Password testPassword = new Password("password123");
        final HashedPassword testHashedPassword = new HashedPassword("hashedTestPassword");
        when(mockPasswordEncoder.hash(testPassword)).thenReturn(testHashedPassword);
        final Role testRole = Role.EMPLOYEE;
        final List<FacultyName> testFaculty = new ArrayList<>();
        testFaculty.add(FacultyName.EEMCS);

        // Act
        registrationService.registerUser(testUser, testPassword, testRole, testFaculty);

        // Assert
        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(testHashedPassword);
        assertThat(savedUser.getRole()).isEqualTo(testRole);
        assertThat(savedUser.getFaculty()).isEqualTo(testFaculty);
    }

    @Test
    public void createUser_withExistingUser_throwsException() {
        // Arrange
        final NetId testUser = new NetId("SomeUser");
        final HashedPassword existingTestPassword = new HashedPassword("password123");
        final Password newTestPassword = new Password("password456");
        final Role testRole = Role.EMPLOYEE;
        final List<FacultyName> testFaculty = new ArrayList<>();
        testFaculty.add(FacultyName.EEMCS);

        AppUser existingAppUser = new AppUser(testUser, existingTestPassword, testRole, testFaculty);
        userRepository.save(existingAppUser);

        // Act
        ThrowingCallable action = () -> registrationService.registerUser(testUser, newTestPassword, testRole, testFaculty);

        // Assert
        assertThatExceptionOfType(Exception.class)
                .isThrownBy(action);

        AppUser savedUser = userRepository.findByNetId(testUser).orElseThrow();

        assertThat(savedUser.getNetId()).isEqualTo(testUser);
        assertThat(savedUser.getPassword()).isEqualTo(existingTestPassword);
        assertThat(savedUser.getRole()).isEqualTo(testRole);
        assertThat(savedUser.getFaculty()).isEqualTo(testFaculty);
    }

    //@AfterAll
    //static void afterAll(@Autowired ApplicationContext applicationContext) {
    //    EmbeddedKafkaBroker embeddedKafkaBroker = applicationContext.getBean(EmbeddedKafkaBroker.class);
    //    embeddedKafkaBroker.destroy();
    //}
}
