package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.user.authentication.JwtUserDetailsService;
import nl.tudelft.sem.template.user.domain.user.*;
import nl.tudelft.sem.template.user.domain.user.FacultyName;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainUserController {

    private final transient AuthenticationManager authenticationManager;

    private final transient JwtTokenGenerator jwtTokenGenerator;

    private final transient JwtUserDetailsService jwtUserDetailsService;

    private final transient RegistrationService registrationService;

    /**
     * Instantiates a new Main user controller.
     *
     * @param authenticationManager the authentication manager
     * @param jwtTokenGenerator     the jwt token generator
     * @param jwtUserDetailsService the jwt user details service
     * @param registrationService   the registration service
     */
    public MainUserController(
            AuthenticationManager authenticationManager,
            JwtTokenGenerator jwtTokenGenerator,
            JwtUserDetailsService jwtUserDetailsService,
            RegistrationService registrationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.registrationService = registrationService;
    }

    /**
     * Add new user status dto.
     *
     * @param userDTO the user dto
     * @return the status dto
     */
    @KafkaListener(
            topics = "add-user-topic",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryUserDTO"
    )
    @SendTo
    public StatusDTO addNewUser(UserDTO userDTO) {
        StatusDTO result = checkStatusDTOValidity(userDTO);
        if (result != null) {
            return result;
        }

        NetId netId = new NetId(userDTO.getNetId());
        Password password = new Password(userDTO.getPassword());
        Role role = Role.valueOf(userDTO.getRole());

        List<String> facultiesStrings = userDTO.getFaculties();
        List<nl.tudelft.sem.template.user.domain.user.FacultyName> faculties = new ArrayList<>();

        result = fillFaculties(facultiesStrings, faculties);
        if (result != null) {
            return result;
        }

        try {
            registrationService.registerUser(netId, password, role, faculties);
        } catch (NetIdAlreadyInUseException e) {
            return new StatusDTO("User with netID: " + e.getMessage() + " already exists");
        }

        return new StatusDTO("OK");
    }

    /**
     * Checks if the inputted UserDTO doesn't have any empty fields.
     * @param userDTO - the userDTO to check
     * @return null if everything is fine and a statusDTO with the error otherwise.
     */
    public StatusDTO checkStatusDTOValidity(UserDTO userDTO) {
        if (userDTO.getNetId().equals("")) {
            return new StatusDTO("NetId cannot be empty!");
        }
        if (userDTO.getPassword().equals("")) {
            return new StatusDTO("Password cannot be empty!");
        }
        try {
            Role unused = Role.valueOf(userDTO.getRole());
        } catch (IllegalArgumentException e) {
            return new StatusDTO("Provided role does not exist!");
        }
        return null;
    }

    /**
     * This method fills the faculties List with the actual faculties.
     * @param facultiesStrings - the faculties to be added to the list
     * @param faculties - empty list of faculties that will be filled
     * @return null if everything is fine, and a statusDTO with error message if something is not right
     */
    public StatusDTO fillFaculties(List<String> facultiesStrings, List faculties) {
        for (String name : facultiesStrings) {
            try {
                faculties.add(FacultyName.valueOf(name));
            } catch (IllegalArgumentException e) {
                return new StatusDTO("Wrong faculty name");
            }
        }
        return null;
    }


    /**
     * Authenticate user token dto.
     *
     * @param userCredentials the user credentials
     * @return the token dto
     */
    @KafkaListener(
            topics = "user-auth-topic",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryUserCredentials"
    )
    @SendTo
    public TokenDTO authenticateUser(UserCredentials userCredentials) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userCredentials.getNetId(),
                            userCredentials.getPassword()));
        } catch (DisabledException e) {
            return new TokenDTO("USER_DISABLED", "");
        } catch (BadCredentialsException e) {
            return new TokenDTO("INVALID_CREDENTIALS", "");
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userCredentials.getNetId());
        final String jwtToken = jwtTokenGenerator.generateToken(userDetails);
        return new TokenDTO("OK", jwtToken);
    }
}
