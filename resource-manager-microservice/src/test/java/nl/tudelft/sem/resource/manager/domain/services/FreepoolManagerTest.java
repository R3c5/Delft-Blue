package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.DefaultResources;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.node.*;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourceId;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResources;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.commons.OwnerName;
import sem.commons.Token;
import sem.commons.URL;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class FreepoolManagerTest {
    private transient FreepoolManager sut;
    private transient DefaultResources defaultResources;
    @Mock
    private transient ReservedResourcesRepository reservedResourcesRepository;
    @Mock
    private transient NodeRepository nodeRepository;

    @BeforeEach
    void setUp() {
        defaultResources = new DefaultResources();
        sut = new FreepoolManager(defaultResources, reservedResourcesRepository, nodeRepository);
    }

    @Test
    void get_available_freepool_resources() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        ReservedResourceId id = new ReservedResourceId(date, Reserver.FREEPOOL);
        ReservedResources reservedResources = new ReservedResources(
                id,
                new Resource(50, 40, 30)
        );

        Mockito.when(reservedResourcesRepository.findById(id))
                .thenReturn(Optional.of(reservedResources));

        ClusterNode node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                new Resource(30, 30, 30));

        ClusterNode node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                new Resource(70, 70, 70));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));

        Resource availableResources = sut.getAvailableResources(date);

        assertThat(availableResources)
                .isEqualTo(new Resource(50, 60, 70));
    }

    @Test
    void get_available_resources_empty_cluster() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        ReservedResourceId id = new ReservedResourceId(date, Reserver.FREEPOOL);
        ReservedResources reservedResources = new ReservedResources(
                id,
                new Resource(50, 40, 30)
        );

        Mockito.when(reservedResourcesRepository.findById(id))
                .thenReturn(Optional.of(reservedResources));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of());

        Resource availableResources = sut.getAvailableResources(date);

        assertThat(availableResources)
                .isEqualTo(new Resource(-50, -40, -30));
    }

    @Test
    void get_available_resources_no_reserved_resources() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        ReservedResourceId id = new ReservedResourceId(date, Reserver.FREEPOOL);

        Mockito.when(reservedResourcesRepository.findById(id))
                .thenReturn(Optional.empty());

        ClusterNode node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                new Resource(30, 30, 30));

        ClusterNode node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                new Resource(70, 70, 70));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));

        Resource availableResources = sut.getAvailableResources(date);

        assertThat(availableResources)
                .isEqualTo(new Resource(100, 100, 100));
    }
}