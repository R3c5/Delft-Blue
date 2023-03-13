package nl.tudelft.sem.resource.manager.domain;

import org.springframework.stereotype.Component;

@Component
public class DefaultResources {
    // The amount of resources always available to each faculty
    private final Resource initialResources;

    public DefaultResources() {
        this.initialResources = new Resource(1000, 1000, 1000);
    }

    public DefaultResources(int resourceCount) {
        this.initialResources = new Resource(resourceCount, resourceCount, resourceCount);
    }

    public Resource getInitialResources() {
        return initialResources;
    }
}
