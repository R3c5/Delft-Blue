package nl.tudelft.sem.template.user.models;

import lombok.Data;
import nl.tudelft.sem.template.user.domain.user.FacultyName;

import java.util.List;

/**
 * Model representing an authentication request.
 */
@Data
public class AuthenticationRequestModel {
    private String netId;
    private String password;
    private String role;
    private List<FacultyName> faculty;
}