package nl.tudelft.sem.template.user.controllers;

import nl.tudelft.sem.template.user.authentication.JwtTokenGenerator;
import nl.tudelft.sem.template.user.authentication.JwtUserDetailsService;
import nl.tudelft.sem.template.user.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import sem.commons.StatusDTO;
import sem.commons.TokenDTO;
import sem.commons.UserCredentials;
import sem.commons.UserDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MainUserControllerTest {

    private AuthenticationManager authenticationManager;

    private JwtTokenGenerator jwtTokenGenerator;

    private JwtUserDetailsService jwtUserDetailsService;

    private RegistrationService registrationService;

    private MainUserController mainUserController;

    private UserDTO user;

    private UserCredentials uc;

    @BeforeEach
    void setUp() {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtTokenGenerator = Mockito.mock(JwtTokenGenerator.class);
        jwtUserDetailsService = Mockito.mock(JwtUserDetailsService.class);
        registrationService = Mockito.mock(RegistrationService.class);
        mainUserController = new MainUserController(
                authenticationManager, jwtTokenGenerator, jwtUserDetailsService, registrationService
        );
        user = new UserDTO("netId", "pass", "EMPLOYEE", List.of("EEMCS"));
        uc = new UserCredentials("netId", "pass");
    }

    @Test
    void addNewUserCorrect() throws NetIdAlreadyInUseException {
        StatusDTO status = mainUserController.addNewUser(user);
        verify(registrationService, times(1)).registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any());
        assertEquals("OK", status.getStatus());
    }

    @Test
    void addNewUserEmptyNetId() throws NetIdAlreadyInUseException {
        user.setNetId("");
        StatusDTO status = mainUserController.addNewUser(user);
        verify(registrationService, times(0)).registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any());
        assertEquals("NetId cannot be empty!", status.getStatus());
    }

    @Test
    void addNewUserEmptyPassword() throws NetIdAlreadyInUseException {
        user.setPassword("");
        StatusDTO status = mainUserController.addNewUser(user);
        verify(registrationService, times(0)).registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any());
        assertEquals("Password cannot be empty!", status.getStatus());
    }

    @Test
    void addNewUserWrongRole() throws NetIdAlreadyInUseException {
        user.setRole("FOO");
        StatusDTO status = mainUserController.addNewUser(user);
        verify(registrationService, times(0)).registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any());
        assertEquals("Provided role does not exist!", status.getStatus());
    }

    @Test
    void addNewUserWrongFaculty() throws NetIdAlreadyInUseException {
        user.setFaculties(List.of("FOO"));
        StatusDTO status = mainUserController.addNewUser(user);
        verify(registrationService, times(0)).registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any());
        assertEquals("Wrong faculty name", status.getStatus());
    }

    @Test
    void addNewUserTwice() throws NetIdAlreadyInUseException {
        when(registrationService.registerUser(
                any(NetId.class), any(Password.class), any(Role.class), any()))
                .thenThrow(new NetIdAlreadyInUseException(new NetId("netId")));

        StatusDTO status = mainUserController.addNewUser(user);

        assertEquals("User with netID: netId already exists", status.getStatus());
    }



    @Test
    void authenticateUserCorrect() {
        UserDetails userDetails = new User("netId", "pass", List.of());
        when(jwtUserDetailsService.loadUserByUsername(uc.getNetId()))
                .thenReturn(userDetails);
        when(jwtTokenGenerator.generateToken(userDetails)).thenReturn("token");

        TokenDTO tokenDTO = mainUserController.authenticateUser(uc);
        assertEquals("OK", tokenDTO.getStatus());
        assertEquals("token", tokenDTO.getToken());
    }

    @Test
    void authenticateUserDisabledException() {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("msg"));

        TokenDTO tokenDTO = mainUserController.authenticateUser(uc);
        assertEquals("USER_DISABLED", tokenDTO.getStatus());
        assertEquals("", tokenDTO.getToken());
    }

    @Test
    void authenticateUserBadCredentialsException() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("msg"));

        TokenDTO tokenDTO = mainUserController.authenticateUser(uc);
        assertEquals("INVALID_CREDENTIALS", tokenDTO.getStatus());
        assertEquals("", tokenDTO.getToken());
    }
}