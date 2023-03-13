package nl.tudelft.sem.resource.manager.domain;

import lombok.Data;
import nl.tudelft.sem.resource.manager.domain.node.NodeRepository;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import org.springframework.stereotype.Component;

@Component
@Data
public class RepositoriesGroup {

    private final transient NodeRepository nodeRepository;
    private final transient ReservedResourcesRepository reservedResourcesRepository;

}
