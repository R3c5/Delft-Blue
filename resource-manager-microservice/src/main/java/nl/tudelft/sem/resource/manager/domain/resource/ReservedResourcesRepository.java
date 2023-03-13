package nl.tudelft.sem.resource.manager.domain.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedResourcesRepository extends JpaRepository<ReservedResources, ReservedResourceId> {
    List<ReservedResources> findAllById_Date(LocalDate date);
}
