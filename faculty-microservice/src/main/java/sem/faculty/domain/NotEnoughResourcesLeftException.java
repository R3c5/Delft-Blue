package sem.faculty.domain;

/**
 * Exception to indicate the that a request can not be scheduled.
 * */
public class NotEnoughResourcesLeftException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NotEnoughResourcesLeftException(long requestId) {
        super(String.valueOf(requestId));
    }
}
