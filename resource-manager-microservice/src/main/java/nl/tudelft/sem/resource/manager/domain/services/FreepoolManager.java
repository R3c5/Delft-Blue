package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.DefaultResources;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.node.ClusterNode;
import nl.tudelft.sem.resource.manager.domain.node.NodeRepository;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourceId;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResources;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Manages the resources in the free pool.
 */
@Service
public class FreepoolManager {
    private final transient DefaultResources defaultResources;
    private final transient ReservedResourcesRepository reservedResourcesRepository;
    private final transient NodeRepository nodeRepository;

    /**
     * Injects dependencies.
     *
     * @param defaultResources DefaultResources
     * @param reservedResourcesRepository ResourcesRepository
     * @param nodeRepository NodeRepository
     */
    @Autowired
    public FreepoolManager(DefaultResources defaultResources,
                           ReservedResourcesRepository reservedResourcesRepository,
                           NodeRepository nodeRepository) {
        this.defaultResources = defaultResources;
        this.reservedResourcesRepository = reservedResourcesRepository;
        this.nodeRepository = nodeRepository;
    }

    /**
     * Returns how many resources are available in the freepool at a given date.
     * @param date a {@link LocalDate} representing the date to check for
     * @return what Resources are available
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public Resource getAvailableResources(LocalDate date) {
        Resource freepoolSize = nodeRepository
                .findAll()
                .stream()
                .map(ClusterNode::getResources)
                .reduce(new Resource(), Resource::add);

        Resource usedResources = reservedResourcesRepository
                .findById(new ReservedResourceId(date, Reserver.FREEPOOL))
                .map(ReservedResources::getResources)
                .orElse(new Resource(0, 0, 0));

        return Resource.sub(freepoolSize, usedResources);
    }
}
