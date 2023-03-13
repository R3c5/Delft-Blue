package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterNodeDTOTest {

    private Token token;
    private OwnerName ownerName;
    private URL url;
    private Resource resources;
    private ClusterNodeDTO clusterNodeDTO;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        this.token = new Token("tokenName");
        this.ownerName = new OwnerName("onwerName");
        this.url = new URL("url");
        this.resources = new Resource(3, 2, 1);
        this.clusterNodeDTO = new ClusterNodeDTO(token, ownerName, url, resources);
    }

    @Test
    void getToken() {
        assertThat(clusterNodeDTO.getToken()).isEqualTo(token);
    }

    @Test
    void getOwnerName() {
        assertThat(clusterNodeDTO.getOwnerName()).isEqualTo(ownerName);
    }

    @Test
    void getUrl() {
        assertThat(clusterNodeDTO.getUrl()).isEqualTo(url);
    }

    @Test
    void getResources() {
        assertThat(clusterNodeDTO.getResources()).isEqualTo(resources);
    }

    @Test
    void setToken() {
        Token newToken = new Token("newToken");
        clusterNodeDTO.setToken(newToken);
        assertThat(clusterNodeDTO.getToken()).isEqualTo(newToken);
    }

    @Test
    void setOwnerName() {
        OwnerName newOwnerName = new OwnerName("newOwnerName");
        clusterNodeDTO.setOwnerName(newOwnerName);
        assertThat(clusterNodeDTO.getOwnerName()).isEqualTo(newOwnerName);
    }

    @Test
    void setUrl() {
        URL newUrl = new URL("newUrl");
        clusterNodeDTO.setUrl(newUrl);
        assertThat(clusterNodeDTO.getUrl()).isEqualTo(newUrl);
    }

    @Test
    void setResources() throws NotValidResourcesException {
        Resource newResource = new Resource(5, 4, 3);
        clusterNodeDTO.setResources(newResource);
        assertThat(clusterNodeDTO.getResources()).isEqualTo(newResource);
    }

    @Test
    void testEquals() throws NotValidResourcesException {
        ClusterNodeDTO newClusterNodeDTO = new ClusterNodeDTO(new Token("tokenName"),
                new OwnerName("ownerName"), new URL("url"), new Resource(3, 2, 1));
        assertThat(clusterNodeDTO).isEqualTo(newClusterNodeDTO);
        assertThat(clusterNodeDTO).isEqualTo(clusterNodeDTO);
    }

    @Test
    void testHashCode() throws NotValidResourcesException {
        ClusterNodeDTO newClusterNodeDTO = new ClusterNodeDTO(new Token("tokenName"),
                new OwnerName("ownerName"), new URL("url"), new Resource(3, 2, 1));
        assertThat(clusterNodeDTO.hashCode()).isEqualTo(newClusterNodeDTO.hashCode());
    }
}