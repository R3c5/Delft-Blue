package nl.tudelft.sem.resource.manager.domain.node;

import nl.tudelft.sem.resource.manager.domain.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.commons.OwnerName;
import sem.commons.Token;
import sem.commons.URL;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class NodeRepositoryTest {
    @Autowired
    private transient NodeRepository nodeRepository;
    private transient ClusterNode node1;
    private transient ClusterNode node2;

    @BeforeEach
    void setUp() {
        node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                new Resource(10, 10, 10)
        );

        node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                new Resource(20, 20, 20)
        );
    }

    @Test
    void exists_by_token_test() {
        nodeRepository.saveAll(List.of(node1));

        assertThat(nodeRepository.existsByToken(node1.getToken())).isTrue();
        assertThat(nodeRepository.existsByToken(node2.getToken())).isFalse();
    }

    @Test
    void remove_by_token_test() {
        nodeRepository.saveAll(List.of(node1));

        assertThat(nodeRepository.existsByToken(node1.getToken())).isTrue();
        assertThat(nodeRepository.existsByToken(node2.getToken())).isFalse();

        nodeRepository.removeByToken(node1.getToken());

        assertThat(nodeRepository.existsByToken(node1.getToken())).isFalse();
        assertThat(nodeRepository.existsByToken(node2.getToken())).isFalse();
    }

    @Test
    void get_all_test() {
        nodeRepository.saveAll(List.of(node1));

        assertThat(nodeRepository.findAll())
                .contains(node1)
                .doesNotContain(node2);
    }
}
