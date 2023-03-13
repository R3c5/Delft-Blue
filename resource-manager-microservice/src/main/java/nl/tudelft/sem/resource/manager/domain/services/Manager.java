package nl.tudelft.sem.resource.manager.domain.services;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.node.ClusterNode;
import nl.tudelft.sem.resource.manager.domain.node.NodeRepository;
import nl.tudelft.sem.resource.manager.domain.node.exceptions.NodeNotFoundException;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import org.springframework.stereotype.Service;
import sem.commons.FacultyNameDTO;
import sem.commons.NotValidResourcesException;
import sem.commons.Token;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade that provides the functionality needed by controllers.
 */
@Service
@AllArgsConstructor
public class Manager {
    private final transient DateSchedulingService dateSchedulingService;
    private final transient ResourceHandler resourceHandler;
    private final transient ResourceAvailabilityService resourceAvailabilityService;
    private final transient NodeRepository nodeRepository;

    /**
     * Retrieves the closest available date when the given resources are available.
     *
     * @param resources the amount of resources to be reserved
     * @param date the date by which the resources need to be reserved
     * @param facultyName the name of the faculty for which the reservation should be made
     * @return a LocalDate that represents when the reservation can be made, or null
     *          if there is no opening by that date
     */
    public LocalDate getDateForRequest(Resource resources, LocalDate date, Reserver facultyName) {
        return dateSchedulingService.getDateForRequest(resources, date, facultyName);
    }

    /**
     * Releases a faculty's resources on a list of days. This is done by
     * adding the amount of free resources left for the faculty on that day,
     * and subtracting it from the freepool's reserved resources.
     * @param faculty the faculty that has frees its resources
     * @param releasedDays the days on which to release the resources
     */
    public void releaseResourcesOnDays(Reserver faculty, List<LocalDate> releasedDays) {
        resourceHandler.releaseResourcesOnDays(faculty, releasedDays);
    }

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
    public void reserveResourcesOnDay(
            Reserver faculty,
            Resource requestedResources,
            LocalDate date
    ) throws NotEnoughResourcesException {
        resourceHandler.reserveResourcesOnDay(faculty, requestedResources, date);
    }

    /**
     * Returns the free resources on a given date.
     * @param date the date on which to get the free resources
     * @return the free resources
     */
    public Resource seeFreeResourcesOnDate(LocalDate date) {
        return resourceAvailabilityService.seeFreeResourcesOnDate(date);
    }

    /**
     * Returns the amount of free {@link Resource Resources} available the following day
     * for a given faculty. This also includes the resources available in the free pool.
     *
     * @param faculty the faculty for which to get the resources
     * @return the amount of free resources
     */
    public Resource seeFreeResourcesTomorrow(Reserver faculty) {
        return resourceAvailabilityService.seeFreeResourcesTomorrow(faculty);
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
        return resourceAvailabilityService.seeReservedResourcesOnDate(date);
    }

    /**
     * Adds the node to the cluster.
     * @param node the node to add to the cluster
     * @return a message indicating whether a node with the same token already exists or not
     */
    public String addNodeToCluster(ClusterNode node) {
        if (nodeRepository.existsByToken(node.getToken())) {
            return "Node with token " + node.getToken() + " already exists!";
        }
        nodeRepository.save(node);
        return "OK";
    }

    /**
     * Removes a node from the cluster.
     * @param token the token by which to find the node
     * @throws NodeNotFoundException if there is no node with that token in the cluster
     */
    public void removeNodeFromCluster(Token token) throws NodeNotFoundException {
        if (!nodeRepository.existsByToken(token)) {
            throw new NodeNotFoundException(token);
        }
        nodeRepository.removeByToken(token);
    }

    /**
     * This method will retrieve all free resources from all faculties.
     * @return a map containing the available resources for all faculties
     */
    public Map<FacultyNameDTO, sem.commons.Resource> getAvailableResourcesForAllFacultiesOnDate() {
        Map<FacultyNameDTO, sem.commons.Resource> availableResources = new HashMap<>();

        EnumSet.allOf(Reserver.class)
                .forEach(faculty -> {
                    Resource resourceObject = seeFreeResourcesTomorrow(faculty);

                    try {
                        availableResources.put(new FacultyNameDTO(faculty.toString()), new sem.commons
                                .Resource(resourceObject.getCpuResources(), resourceObject.getGpuResources(),
                                resourceObject.getMemResources()));
                    } catch (NotValidResourcesException e) {
                        throw new RuntimeException(e);
                    }
                });

        return availableResources;
    }
}
