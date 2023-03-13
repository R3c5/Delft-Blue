package nl.tudelft.sem.resource.manager.domain.services;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.resource.manager.domain.DefaultResources;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourceId;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResources;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service that handles reserving resources for a request, and releasing a faculty's
 * resources for a time period. Also provides a callback that checks whether a node
 * being removed makes it so there are insufficient resources left for the planned
 * requests
 */
@Service
@AllArgsConstructor
public class ResourceHandler {
    private final transient ResourceAvailabilityService resourceAvailabilityService;
    private final transient ReservedResourcesRepository reservedResourcesRepository;
    private final transient DefaultResources defaultResources;

    /**
     * Reserve resources for a request from a faculty on a given date.
     * May reserve resources from the freepool if there are not enough free resources
     * allocated to the faculty
     *
     * @param faculty the faculty for which to reserve resources from
     * @param requestedResources the amount of resources to reserve
     * @param date the date on which to reserve the resources
     * @throws NotEnoughResourcesException if there are not enough resources on that day
     */
    void reserveResourcesOnDay(Reserver faculty,
                                      Resource requestedResources,
                                      LocalDate date) throws NotEnoughResourcesException {
        Resource freeResources = resourceAvailabilityService.seeFreeResourcesByDateAndReserver(date, faculty);

        // Not enough resources to reserve the request
        if (requestedResources.getCpuResources() > freeResources.getCpuResources() ||
            requestedResources.getGpuResources() > freeResources.getGpuResources() ||
            requestedResources.getMemResources() > freeResources.getMemResources()) {
            throw new NotEnoughResourcesException(date, requestedResources);
        }

        // Free resources left for the faculty
        Resource freeFacultyResources = reservedResourcesRepository
                .findById(new ReservedResourceId(date, faculty))
                .map(r -> Resource.sub(defaultResources.getInitialResources(), r.getResources()))
                .orElse(defaultResources.getInitialResources());

        updateReservedResources(date, faculty, new Resource(
                Math.min(freeFacultyResources.getCpuResources(), requestedResources.getCpuResources()),
                Math.min(freeFacultyResources.getGpuResources(), requestedResources.getGpuResources()),
                Math.min(freeFacultyResources.getMemResources(), requestedResources.getMemResources())
        ));

        Resource leftoverResources = Resource.sub(requestedResources, freeFacultyResources);
        updateReservedResources(date, Reserver.FREEPOOL, new Resource(
                Math.max(0, leftoverResources.getCpuResources()),
                Math.max(0, leftoverResources.getGpuResources()),
                Math.max(0, leftoverResources.getMemResources())
        ));
    }

    /**
     * Releases a faculty's resources on a list of days. This is done by
     * adding the amount of free resources left for the faculty on that day,
     * and subtracting it from the freepool's reserved resources.
     * @param faculty the faculty that has frees its resources
     * @param releasedDays the days on which to release the resources
     */
    void releaseResourcesOnDays(Reserver faculty,
                                       List<LocalDate> releasedDays) {
        for (LocalDate day : releasedDays) {
            ReservedResourceId id = new ReservedResourceId(day, faculty);
            Resource remainingFacultyResources = reservedResourcesRepository
                    .findById(id)
                    .map(ReservedResources::getResources)
                    .map(r -> Resource.sub(defaultResources.getInitialResources(), r))
                    .orElse(Resource.with(0));

            updateReservedResources(day, faculty, remainingFacultyResources);
            updateReservedResources(day, Reserver.FREEPOOL, Resource.sub(Resource.with(0), remainingFacultyResources));
        }
    }

    /**
     * Checks if there are enough resources in the freepool, on all days
     * with reserved resources. This should be used as a callback in the
     * event that a cluster node is deleted.
     */
    void checkNotEnoughFreepoolResources() {

    }

    /**
     * Updates the amount of reserved resources by adding the newResources to
     * the already existing ones.
     *
     * @param date the date for the ReservedResrouces
     * @param reserver the reserver name for the ReservedResources
     * @param newResources the amount of resources to add
     */
    public void updateReservedResources(LocalDate date, Reserver reserver, Resource newResources) {
        Resource currentResources = reservedResourcesRepository
                .findById(new ReservedResourceId(date, reserver))
                .map(ReservedResources::getResources)
                .orElse(Resource.with(0));

        reservedResourcesRepository.save(new ReservedResources(
                new ReservedResourceId(date, reserver),
                Resource.add(currentResources, newResources)
        ));
    }
}
