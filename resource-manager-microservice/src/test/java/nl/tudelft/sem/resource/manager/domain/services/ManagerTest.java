package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.node.ClusterNode;
import nl.tudelft.sem.resource.manager.domain.node.NodeRepository;
import nl.tudelft.sem.resource.manager.domain.node.exceptions.NodeNotFoundException;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sem.commons.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ManagerTest {
    private transient Manager sut;
    @Mock private transient DateSchedulingService dateSchedulingService;
    @Mock private transient ResourceHandler resourceHandler;
    @Mock private transient ResourceAvailabilityService resourceAvailabilityService;
    @Mock private transient NodeRepository nodeRepository;

    private transient ClusterNode node1;
    private transient ClusterNode node2;
    private transient Resource resource;
    private transient LocalDate localDate;
    private transient sem.commons.Resource commonsResource;


    @BeforeEach
    void setUp() throws NotValidResourcesException {
        sut = new Manager(dateSchedulingService, resourceHandler, resourceAvailabilityService, nodeRepository);

        node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                Resource.with(50)
        );

        node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                Resource.with(60)
        );

        resource = new Resource(3, 2, 1);
        localDate = LocalDate.now();
        commonsResource = new sem.commons.Resource(3, 2, 1);
    }

    @Test
    void see_cluster_node_information_test() {
        when(nodeRepository.findAll()).thenReturn(List.of(node1, node2));

        assertThat(sut.seeClusterNodeInformation())
                .containsExactlyInAnyOrder(node1, node2);
    }

    @Test
    void add_node_to_cluster_test() {
        when(nodeRepository.existsByToken(node1.getToken())).thenReturn(false);

        assertThat(sut.addNodeToCluster(node1)).isEqualTo("OK");

        verify(nodeRepository, times(1)).save(node1);
    }

    @Test
    void addNodeToClusterTokenExists() {
        Mockito.when(nodeRepository.existsByToken(new Token("token1"))).thenReturn(true);
        assertThat(sut.addNodeToCluster(node1)).isEqualTo("Node with token Token(tokenValue=token1) already exists!");
        Mockito.verify(nodeRepository, times(0)).save(node1);
    }

    @Test
    void add_node_to_cluster_fail_test() {
        when(nodeRepository.existsByToken(node1.getToken())).thenReturn(true);

        assertThat(sut.addNodeToCluster(node1)).isNotEqualTo("OK");

        verify(nodeRepository, times(0)).save(node1);
    }

    @Test
    void remove_node_from_cluster_test() throws NodeNotFoundException {
        when(nodeRepository.existsByToken(node1.getToken())).thenReturn(true);

        sut.removeNodeFromCluster(node1.getToken());

        verify(nodeRepository, times(1)).removeByToken(node1.getToken());
    }

    @Test
    void remove_node_from_cluster_fails_test() throws NodeNotFoundException {
        when(nodeRepository.existsByToken(node1.getToken())).thenReturn(false);

        assertThatThrownBy(() -> {
            sut.removeNodeFromCluster(node1.getToken());
        }).isInstanceOf(NodeNotFoundException.class);

        verify(nodeRepository, times(0)).removeByToken(node1.getToken());
    }

    @Test
    void getDateForRequestTest() {
        Mockito.when(dateSchedulingService.getDateForRequest(resource, localDate, Reserver.AE)).thenReturn(localDate);
        LocalDate date = sut.getDateForRequest(resource, localDate, Reserver.AE);
        assertThat(date).isEqualTo(localDate);
        Mockito.verify(dateSchedulingService).getDateForRequest(resource, localDate, Reserver.AE);
    }

    @Test
    void seeFreeResourceOnDateTest() {
        Mockito.when(resourceAvailabilityService.seeFreeResourcesOnDate(localDate)).thenReturn(resource);
        Resource returnedResource = sut.seeFreeResourcesOnDate(localDate);
        assertThat(returnedResource).isEqualTo(resource);
        Mockito.verify(resourceAvailabilityService).seeFreeResourcesOnDate(localDate);
    }

    @Test
    void releaseResourceOnDaysTest() {
        sut.releaseResourcesOnDays(Reserver.ARCH, List.of(localDate));
        Mockito.verify(resourceHandler).releaseResourcesOnDays(Reserver.ARCH, List.of(localDate));
    }

    @Test
    void reserveResourcesOnDayTest() throws NotEnoughResourcesException {
        sut.reserveResourcesOnDay(Reserver.AE, resource, localDate);
        Mockito.verify(resourceHandler).reserveResourcesOnDay(Reserver.AE, resource, localDate);
    }

    @Test
    void seeFreeResourcesTomorrowTest() {
        sut.seeFreeResourcesTomorrow(Reserver.AE);
        Mockito.verify(resourceAvailabilityService).seeFreeResourcesTomorrow(Reserver.AE);
    }

    @Test
    void seeReservedResourcesOnDateTest() {
        sut.seeReservedResourcesOnDate(localDate);
        Mockito.verify(resourceAvailabilityService).seeReservedResourcesOnDate(localDate);
    }

    @Test
    void testAvailableResourcesForAllFacultiesTest() {
        Mockito.when(resourceAvailabilityService.seeFreeResourcesTomorrow(Mockito.any(Reserver.class)))
                .thenReturn(resource);
        Map<FacultyNameDTO, sem.commons.Resource> availableResources = new HashMap<>();
        for (Reserver r : Reserver.values()) {
            availableResources.put(new FacultyNameDTO(r.toString()), commonsResource);
        }
        Map<FacultyNameDTO, sem.commons.Resource> returnedAvailableResources =
                sut.getAvailableResourcesForAllFacultiesOnDate();
        assertThat(returnedAvailableResources).isEqualTo(availableResources);
    }

}