package nl.tudelft.sem.resource.manager.domain.resource.exceptions;

import nl.tudelft.sem.resource.manager.domain.Resource;

import java.time.LocalDate;

public class NotEnoughResourcesException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    /**
     * Exception constructor.
     *
     * @param date the date
     * @param resource the amount of resources that could not be reserved
     */
    public NotEnoughResourcesException(LocalDate date,
                                       Resource resource) {
        super("Resources " +
                resource +
                " could not be allocated on " +
                date +
                ": not enough free resources");
    }
}
