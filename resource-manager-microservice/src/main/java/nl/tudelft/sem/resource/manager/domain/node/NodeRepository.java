package nl.tudelft.sem.resource.manager.domain.node;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.commons.Token;

import java.util.List;
import java.util.Optional;

/**
 * A DDD Repository for querying information about the cluster Nodes.
 */

@Repository
public interface NodeRepository extends JpaRepository<ClusterNode, Long> {
    boolean existsByToken(Token token);

    void removeByToken(Token token);
}
