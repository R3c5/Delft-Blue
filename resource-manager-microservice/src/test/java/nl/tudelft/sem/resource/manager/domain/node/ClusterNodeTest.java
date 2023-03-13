package nl.tudelft.sem.resource.manager.domain.node;

import nl.tudelft.sem.resource.manager.domain.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sem.commons.OwnerName;
import sem.commons.Token;
import sem.commons.URL;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterNodeTest {

    private transient ClusterNode node1;
    private transient ClusterNode node2;

    @BeforeEach
    void setUp() {
        node1 = new ClusterNode(
                new OwnerName("name1"),
                new URL("url1"),
                new Token("token1"),
                Resource.with(100)
        );

        node2 = new ClusterNode(
                new OwnerName("name2"),
                new URL("url2"),
                new Token("token2"),
                Resource.with(200)
        );
    }

    @Test
    void test_equals() {
        ClusterNode node3 = new ClusterNode(
                new OwnerName("name3"),
                new URL("url3"),
                new Token("token1"),
                Resource.with(300)
        );

        assertThat(node1).isEqualTo(node3);
    }

    @Test
    void test_equals_fail() {
        assertThat(node1).isNotEqualTo(node2);
    }

    @Test
    void test_equals_wrong_object() {
        assertThat(node1).isNotEqualTo("a");
    }

    @Test
    void test_hash_code() {
        assertThat(node1.hashCode()).isEqualTo(node1.hashCode());
    }

    @Test
    void test_hash_code_fails() {
        assertThat(node1.hashCode()).isNotEqualTo(node2.hashCode());
    }
}