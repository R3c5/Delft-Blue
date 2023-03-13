package sem.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTest {

    @Test
    void testEmptyConstructor() {
        Resource resource = new Resource();
        assertEquals(0, resource.getCpu());
        assertEquals(0, resource.getGpu());
        assertEquals(0, resource.getMemory());
    }

    @Test
    void testResource() {
        Resource resource;
        try {
            resource = new Resource(8, 2, 4);
        } catch (Exception e) {
            resource = null;
        }

        // Test if the values are stored correctly
        assertEquals(8, resource.getCpu());
        assertEquals(2, resource.getGpu());
        assertEquals(4, resource.getMemory());
    }

    //Tests for business logic cpu >= max(memory, gpu)
    @Test
    void cpuLowerThanGpu() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(2, 3, 1);
        });
        Exception expectedException = new Exception("The cpu resources should be equal to at least max(memory, gpu)");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }

    @Test
    void cpuLowerThanMemory() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(2, 1, 3);
        });
        Exception expectedException = new Exception("The cpu resources should be equal to at least max(memory, gpu)");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }

    @Test
    void cpuEqualToGpu() {
        Resource resource;
        try {
            resource = new Resource(2, 2, 1);
        } catch (Exception e) {
            resource = null;
        }
        assert (resource != null);
    }

    @Test
    void cpuEqualToMemory() {
        Resource resource;
        try {
            resource = new Resource(2, 1, 2);
        } catch (Exception e) {
            resource = null;
        }
        assert (resource != null);
    }

    @Test
    void allEqualResources() {
        Resource resource;
        try {
            resource = new Resource(1, 1, 1);
        } catch (Exception e) {
            resource = null;
        }
        assert (resource != null);
    }

    //Tests for making sure all resource values are >= 0
    @Test
    void cpuIsNegative() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(-1, 0, 0);
        });
        Exception expectedException = new Exception("Resource cannot have negative values");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }

    @Test
    void gpuIsNegative() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(0, -1, 0);
        });
        Exception expectedException = new Exception("Resource cannot have negative values");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }

    @Test
    void memoryIsNegative() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(0, 0, -1);
        });
        Exception expectedException = new Exception("Resource cannot have negative values");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }

    @Test
    void allNegative() {
        Exception thrownException = assertThrows(Exception.class, () -> {
            Resource resource = new Resource(-1, -1, -1);
        });
        Exception expectedException = new Exception("Resource cannot have negative values");
        assertEquals(thrownException.getMessage(), expectedException.getMessage());
    }
}
