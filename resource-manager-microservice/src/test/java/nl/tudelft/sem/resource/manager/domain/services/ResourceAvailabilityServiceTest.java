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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sem.commons.OwnerName;
import sem.commons.Token;
import sem.commons.URL;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceAvailabilityServiceTest {
    private transient ResourceAvailabilityService sut;
    private transient NodeRepository nodeRepository;
    private transient ReservedResourcesRepository reservedResourcesRepository;
    private transient CurrentDateProvider currentDateProvider;
    private transient FreepoolManager freepoolManager;
    private transient DefaultResources defaultResources;

    @BeforeEach
    void setUp() {
        nodeRepository = mock(NodeRepository.class);
        reservedResourcesRepository = mock(ReservedResourcesRepository.class);
        currentDateProvider = mock(CurrentDateProvider.class);
        when(currentDateProvider.getCurrentDate()).thenReturn(LocalDate.of(2022, 1, 1));

        defaultResources = new DefaultResources(100);

        freepoolManager = new FreepoolManager(
                defaultResources,
                reservedResourcesRepository,
                nodeRepository
        );

        sut = new ResourceAvailabilityService(
                new RepositoriesGroup(nodeRepository, reservedResourcesRepository),
                currentDateProvider,
                freepoolManager,
                defaultResources
        );

        LocalDate date = LocalDate.of(2020, 1, 1);
        ReservedResources reservedResourcesAS = new ReservedResources(
                new ReservedResourceId(date, Reserver.AS),
                new Resource(10, 10, 10)
        );
        ReservedResources reservedResourcesARCH = new ReservedResources(
                new ReservedResourceId(date, Reserver.ARCH),
                new Resource(20, 20, 20)
        );
        ReservedResources reservedResourcesPOOL = new ReservedResources(
                new ReservedResourceId(date, Reserver.FREEPOOL),
                new Resource(30, 30, 30)
        );

        LocalDate tmrw = LocalDate.of(2022, 1, 2);
        ReservedResources reservedResourcesArchTmrw = new ReservedResources(
                new ReservedResourceId(tmrw, Reserver.ARCH),
                new Resource(40, 40, 40)
        );
        ReservedResources reservedResourcesPoolTmrw = new ReservedResources(
                new ReservedResourceId(tmrw, Reserver.FREEPOOL),
                new Resource(50, 50, 50)
        );

        when(reservedResourcesRepository.findAllById_Date(date))
                .thenReturn(List.of(
                        reservedResourcesAS,
                        reservedResourcesARCH,
                        reservedResourcesPOOL
                ));
        when(reservedResourcesRepository.findAllById_Date(tmrw))
                .thenReturn(List.of(
                        reservedResourcesArchTmrw,
                        reservedResourcesPoolTmrw
                ));
        when(reservedResourcesRepository.findById(new ReservedResourceId(date, Reserver.FREEPOOL)))
                .thenReturn(Optional.of(reservedResourcesPOOL));
        when(reservedResourcesRepository.findById(new ReservedResourceId(tmrw, Reserver.FREEPOOL)))
                .thenReturn(Optional.of(reservedResourcesPoolTmrw));
        when(reservedResourcesRepository.findById(new ReservedResourceId(tmrw, Reserver.ARCH)))
                .thenReturn(Optional.of(reservedResourcesArchTmrw));

        ClusterNode node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                new Resource(50, 50, 50)
        );
        ClusterNode node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                new Resource(60, 60, 60)
        );
        when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));
    }

    @Test
    void free_resources_on_date_test() {
        assertThat(sut.seeFreeResourcesOnDate(LocalDate.of(2020, 1, 1)))
                .isEqualTo(new Resource(250, 250, 250));
    }

    @Test
    void see_free_resources_tomorrow_test() {
        assertThat(sut.seeFreeResourcesTomorrow(Reserver.ARCH))
                .isEqualTo(new Resource(120, 120, 120));
    }

    @Test
    void see_reserved_resources_on_date_test() {
        assertThat(sut.seeReservedResourcesOnDate(LocalDate.of(2020, 1, 1)))
                .isEqualTo(new Resource(60, 60, 60));
    }
}