package nl.tudelft.sem.resource.manager.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.awt.geom.RectangularShape;
import java.util.Objects;

/**
 * DDD Value Object representing a Node's resources.
 */

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Resource {
    @Column(name = "cpu", nullable = false)
    private int cpuResources;

    @Column(name = "gpu", nullable = false)
    private int gpuResources;

    @Column(name = "memory", nullable = false)
    private int memResources;

    /**
     * Utility method for returning a new Resource with all resources set to the same value.
     * @param resources the value to set all resources to
     * @return the new Resource object
     */
    public static Resource with(int resources) {
        return new Resource(resources, resources, resources);
    }

    /**
     * Utility method for adding 2 {@link Resource Resources}.
     *
     * @param r1 the first addend
     * @param r2 the second addend
     * @return a new {@link Resource} that results from the operation
     */
    public static Resource add(Resource r1, Resource r2) {
        return new Resource(
                r1.cpuResources + r2.cpuResources,
                r1.gpuResources + r2.gpuResources,
                r1.memResources + r2.memResources
        );
    }

    /**
     * Utility method for subtracting 2 {@link Resource Resources}.
     *
     * @param r1 the subtrahend
     * @param r2 the minuend
     * @return a new {@link Resource} that results from the operation
     */
    public static Resource sub(Resource r1, Resource r2) {
        return new Resource(
                r1.cpuResources - r2.cpuResources,
                r1.gpuResources - r2.gpuResources,
                r1.memResources - r2.memResources
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        Resource resource = (Resource) o;
        return cpuResources == resource.cpuResources &&
                gpuResources == resource.gpuResources &&
                memResources == resource.memResources;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpuResources, gpuResources, memResources);
    }
}
