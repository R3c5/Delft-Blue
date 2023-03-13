package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.DefaultResources;
import nl.tudelft.sem.resource.manager.domain.RepositoriesGroup;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.node.ClusterNode;
import nl.tudelft.sem.resource.manager.domain.node.NodeRepository;
import nl.tudelft.sem.resource.manager.domain.providers.implementations.CurrentDateProvider;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourceId;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResources;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResourceAvailabilityService {
    private final transient NodeRepository nodeRepository;
    private final transient ReservedResourcesRepository reservedResourcesRepository;
    private final transient CurrentDateProvider timeProvider;
    private final transient FreepoolManager freepoolManager;
    private final transient DefaultResources defaultResources;

    /**
     * Injects dependencies.
     *
     * @param repositoriesGroup the repositories group
     * @param timeProvider      TimeProvider
     * @param freepoolManager   FreepoolManager
     * @param defaultResources  DefaultResources
     */
    public ResourceAvailabilityService(RepositoriesGroup repositoriesGroup,
                                       CurrentDateProvider timeProvider,
                                       FreepoolManager freepoolManager,
                                       DefaultResources defaultResources) {
        this.nodeRepository = repositoriesGroup.getNodeRepository();
        this.reservedResourcesRepository = repositoriesGroup.getReservedResourcesRepository();
        this.timeProvider = timeProvider;
        this.freepoolManager = freepoolManager;
        this.defaultResources = defaultResources;
    }

    /**
     * Returns the free resources on a given date.
     * @param date the date on which to get the free resources
     * @return the free resources
     */
    public Resource seeFreeResourcesOnDate(LocalDate date) {
        Resource freeFacultyResources = reservedResourcesRepository
                .findAllById_Date(date)
                .stream()
                .filter(rr -> !rr.getId().getReserver().equals(Reserver.FREEPOOL))
                .map(ReservedResources::getResources)
                .map(r -> Resource.sub(defaultResources.getInitialResources(), r))
                .reduce(new Resource(), Resource::add);

        Resource freeFreepoolResources = freepoolManager.getAvailableResources(date);

        return Resource.add(freeFreepoolResources, freeFacultyResources);
    }

    /**
     * Returns the amount of free {@link Resource Resources} available the following day
     * for a given faculty. This also includes the resources available in the free pool.
     *
     * @param faculty the faculty for which to get the resources
     * @return the amount of free resources
     */
    Resource seeFreeResourcesTomorrow(Reserver faculty) {
        LocalDate tomorrow = timeProvider.getCurrentDate().plusDays(1);
        return seeFreeResourcesByDateAndReserver(tomorrow, faculty);
    }

    /**
     * Returns a list of the cluster nodes, for the sysadmin view.
     * @return a list of cluster nodes
     */
    public List<ClusterNode> seeClusterNodeInformation() {
        return nodeRepository
                .findAll();
    }

    /**
     * Returns the reserved resources on a given date.
     * @param date the date on which to get the reserved resources
     * @return the amount of reserved resources
     */
    public Resource seeReservedResourcesOnDate(LocalDate date) {
        return reservedResourcesRepository
                .findAllById_Date(date)
                .stream()
                .map(ReservedResources::getResources)
                .reduce(new Resource(), Resource::add);
    }

    /**
     * Returns the amount of free Resources available to the reserver, on the given date.
     *
     * @param date the date on which to get the amount of free resources
     * @param reserver the entity for which to check the amount of free resources
     * @return the amount of free Resources
     */
    public Resource seeFreeResourcesByDateAndReserver(LocalDate date, Reserver reserver) {
        Resource initialResources = defaultResources.getInitialResources();
        Resource usedFacultyResources = reservedResourcesRepository
                .findById(new ReservedResourceId(date, reserver))
                .map(ReservedResources::getResources)
                .orElse(new Resource(0, 0, 0));
        Resource freeFacultyResources = Resource.sub(initialResources, usedFacultyResources);

        Resource freeFreepoolResources = freepoolManager.getAvailableResources(date);

        return Resource.add(freeFacultyResources, freeFreepoolResources);
    }
}
