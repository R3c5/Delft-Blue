package nl.tudelft.sem.template.gateway.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {

    private transient JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private transient HttpServletRequest mockRequest;
    private transient HttpServletResponse mockResponse;
    private transient AuthenticationException dummyAuthenticationException;

    @BeforeEach
    void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        dummyAuthenticationException = Mockito.mock(AuthenticationException.class);

        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    }

    @Test
    void commence() throws ServletException, IOException {

        jwtAuthenticationEntryPoint.commence(mockRequest, mockResponse, dummyAuthenticationException);

        // Assert
        verifyNoInteractions(mockRequest);
        verify(mockResponse).addHeader(AuthorizationInformation.WWW_AUTHENTICATE_HEADER,
                AuthorizationInformation.AUTHORIZATION_AUTH_SCHEME);
        verify(mockResponse).sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        verifyNoMoreInteractions(mockResponse);
    }
}