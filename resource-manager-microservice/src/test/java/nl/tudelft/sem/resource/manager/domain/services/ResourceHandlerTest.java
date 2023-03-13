package nl.tudelft.sem.resource.manager.domain.services;

import nl.tudelft.sem.resource.manager.domain.DefaultResources;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourceId;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResources;
import nl.tudelft.sem.resource.manager.domain.resource.ReservedResourcesRepository;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ResourceHandlerTest {
    private transient ResourceHandler sut;
    private transient ResourceAvailabilityService resourceAvailabilityService;
    private transient ReservedResourcesRepository reservedResourcesRepository;
    private transient DefaultResources defaultResources;

    @BeforeEach
    void setUp() {
        resourceAvailabilityService = mock(ResourceAvailabilityService.class);
        reservedResourcesRepository = mock(ReservedResourcesRepository.class);
        defaultResources = new DefaultResources(100);
        sut = new ResourceHandler(resourceAvailabilityService, reservedResourcesRepository, defaultResources);
    }

    @Test
    void reserve_resources_on_day_test() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Reserver faculty = Reserver.AE;
        ReservedResourceId facultyId = new ReservedResourceId(date, Reserver.AE);
        ReservedResourceId poolId = new ReservedResourceId(date, Reserver.FREEPOOL);

        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(date, faculty))
                .thenReturn(new Resource(50, 100, 150));
        when(reservedResourcesRepository.findById(facultyId))
                .thenReturn(Optional.of(new ReservedResources(
                        facultyId,
                        new Resource(80, 50, 50))
                //      Free resources: 20, 50, 50
                ));
        try {
            sut.reserveResourcesOnDay(faculty, new Resource(50, 40, 50), date);
        } catch (NotEnoughResourcesException e) {
            e.printStackTrace();
        }

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);

        verify(reservedResourcesRepository, times(2)).save(arg.capture());
        assertThat(arg.getAllValues()).containsExactlyInAnyOrder(
                new ReservedResources(facultyId, new Resource(100, 90, 100)),
                new ReservedResources(poolId, new Resource(30, 0, 0))
        );
    }

    @Test
    void reserve_resources_on_day_insufficient_resources_test() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Reserver faculty = Reserver.AE;
        ReservedResourceId facultyId = new ReservedResourceId(date, Reserver.AE);

        when(resourceAvailabilityService.seeFreeResourcesByDateAndReserver(date, faculty))
                .thenReturn(new Resource(50, 100, 150));
        when(reservedResourcesRepository.findById(facultyId))
                .thenReturn(Optional.of(new ReservedResources(
                                facultyId,
                                new Resource(80, 50, 50))
                //              Free resources: 20, 50, 50
                ));

        assertThatThrownBy(() -> {
            sut.reserveResourcesOnDay(faculty, new Resource(60, 40, 50), date);
        }).isInstanceOf(NotEnoughResourcesException.class);
    }


    @Test
    void release_resources_on_days() {
        LocalDate day1 = LocalDate.of(2022, 1, 1);
        LocalDate day2 = LocalDate.of(2022, 1, 2);
        Reserver faculty = Reserver.AE;

        ReservedResourceId id1 = new ReservedResourceId(day1, faculty);
        ReservedResourceId id2 = new ReservedResourceId(day2, faculty);

        when(reservedResourcesRepository.findById(id1))
                .thenReturn(Optional.of(new ReservedResources(id1, Resource.with(10))));
        when(reservedResourcesRepository.findById(id2))
                .thenReturn(Optional.of(new ReservedResources(id2, Resource.with(20))));

        sut.releaseResourcesOnDays(faculty, List.of(day1, day2));

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);
        verify(reservedResourcesRepository, times(4)).save(arg.capture());

        assertThat(arg.getAllValues()).containsExactlyInAnyOrder(
                new ReservedResources(id1, Resource.with(100)),
                new ReservedResources(id2, Resource.with(100)),
                new ReservedResources(new ReservedResourceId(day1, Reserver.FREEPOOL), Resource.with(-90)),
                new ReservedResources(new ReservedResourceId(day2, Reserver.FREEPOOL), Resource.with(-80))
        );
    }

    @Test
    void release_resources_only_some_days() {
        LocalDate day1 = LocalDate.of(2022, 1, 1);
        LocalDate day2 = LocalDate.of(2022, 1, 2);
        Reserver faculty = Reserver.AE;

        ReservedResourceId id1 = new ReservedResourceId(day1, faculty);
        ReservedResourceId id2 = new ReservedResourceId(day2, faculty);

        when(reservedResourcesRepository.findById(id1))
                .thenReturn(Optional.of(new ReservedResources(id1, Resource.with(10))));
        when(reservedResourcesRepository.findById(id2))
                .thenReturn(Optional.of(new ReservedResources(id2, Resource.with(20))));

        sut.releaseResourcesOnDays(faculty, List.of(day1));

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);
        verify(reservedResourcesRepository, times(2)).save(arg.capture());

        assertThat(arg.getAllValues()).containsExactlyInAnyOrder(
                new ReservedResources(id1, Resource.with(100)),
                new ReservedResources(new ReservedResourceId(day1, Reserver.FREEPOOL), Resource.with(-90))
        );
    }


    @Test
    void update_reserved_resources_empty_initially_test() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Reserver faculty = Reserver.AE;
        when(reservedResourcesRepository.findById(new ReservedResourceId(date, faculty)))
                .thenReturn(Optional.empty());

        sut.updateReservedResources(date, faculty, Resource.with(50));

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);
        verify(reservedResourcesRepository).save(arg.capture());
        assertThat(arg.getValue())
                .isEqualTo(new ReservedResources(
                        new ReservedResourceId(date, faculty),
                        Resource.with(50)
                ));
    }

    @Test
    void update_reserved_resources_with_initial_reserved_resources_test() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Reserver faculty = Reserver.AE;
        when(reservedResourcesRepository.findById(new ReservedResourceId(date, faculty)))
                .thenReturn(Optional.of(new ReservedResources(
                        new ReservedResourceId(date, faculty),
                        Resource.with(40)
                )));

        sut.updateReservedResources(date, faculty, Resource.with(50));

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);
        verify(reservedResourcesRepository).save(arg.capture());
        assertThat(arg.getValue())
                .isEqualTo(new ReservedResources(
                        new ReservedResourceId(date, faculty),
                        Resource.with(90)
                ));
    }

    @Test
    void update_reserved_resources_negative_amount() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Reserver faculty = Reserver.AE;
        when(reservedResourcesRepository.findById(new ReservedResourceId(date, faculty)))
                .thenReturn(Optional.of(new ReservedResources(
                        new ReservedResourceId(date, faculty),
                        Resource.with(40)
                )));

        sut.updateReservedResources(date, faculty, Resource.with(-50));

        ArgumentCaptor<ReservedResources> arg = ArgumentCaptor.forClass(ReservedResources.class);
        verify(reservedResourcesRepository).save(arg.capture());
        assertThat(arg.getValue())
                .isEqualTo(new ReservedResources(
                        new ReservedResourceId(date, faculty),
                        Resource.with(-10)
                ));
    }
}