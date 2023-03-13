package nl.tudelft.sem.template.user.models;

import lombok.Data;
import nl.tudelft.sem.template.user.domain.user.FacultyName;

import java.util.List;

/**
 * Model representing a registration request.
 */
@Data
public class RegistrationRequestModel {
    private String netId;
    private String password;
    private String role;
    private List<FacultyName> faculty;
}