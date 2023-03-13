package nl.tudelft.sem.resource.manager.domain.resource;

import nl.tudelft.sem.resource.manager.domain.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReservedResourcesRepositoryTest {
    @Autowired
    private transient ReservedResourcesRepository reservedResourcesRepository;
    private transient ReservedResources reservedResources1;
    private transient ReservedResources reservedResources2;
    private transient ReservedResources reservedResources3;

    @BeforeEach
    void setUp() {
        reservedResources1 = new ReservedResources(
                new ReservedResourceId(
                        LocalDate.of(2022, 1, 1),
                        Reserver.AE
                ),
                new Resource(10, 10, 10)
        );

        reservedResources2 = new ReservedResources(
                new ReservedResourceId(
                    LocalDate.of(2022, 1, 1),
                    Reserver.CEG
                ),
                new Resource(20, 20, 20)
        );

        reservedResources3 = new ReservedResources(
                new ReservedResourceId(
                    LocalDate.of(2022, 10, 10),
                    Reserver.AE
                ),
                new Resource(40, 40, 40)
        );

        reservedResourcesRepository.save(reservedResources1);
        reservedResourcesRepository.save(reservedResources2);
        reservedResourcesRepository.save(reservedResources3);
    }

    @Test
    void find_by_id_test() {
        assertThat(reservedResourcesRepository.findById(new ReservedResourceId(
                LocalDate.of(2022, 10, 10),
                Reserver.AE
        ))).isEqualTo(Optional.of(reservedResources3));
    }

    @Test
    void find_all_test() {
        List<ReservedResources> reservedResources = reservedResourcesRepository.findAll();

        assertThat(reservedResources).containsExactlyInAnyOrder(
                reservedResources2,
                reservedResources1,
                reservedResources3
        );
    }

    @Test
    void find_all_by_id_date_test() {
        assertThat(reservedResourcesRepository.findAllById_Date(LocalDate.of(2022, 1, 1)))
                .containsExactlyInAnyOrder(reservedResources1, reservedResources2);
    }
}
