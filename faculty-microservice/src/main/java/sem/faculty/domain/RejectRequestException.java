package sem.faculty.domain;

/**
 * Exception to indicate the that a request can not be scheduled.
 */
public class RejectRequestException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public RejectRequestException(long requestId) {
        super(String.valueOf(requestId));
    }
}
