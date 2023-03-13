package sem.faculty.domain;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Parameter Class to reduce number of parameters in Request class.
 */
@Getter
public final class RequestDetails {
    private final String name;
    private final String description;
    private final LocalDate preferredDate;
    private final RequestStatus status;

    /**
     * Constructor method.
     */
    public RequestDetails(String name, String description, LocalDate preferredDate, RequestStatus status) {
        this.name = name;
        this.description = description;
        this.preferredDate = preferredDate;
        this.status = status;
    }
}
