package nl.tudelft.sem.resource.manager.domain.node.exceptions;

import nl.tudelft.sem.resource.manager.domain.node.ClusterNode;
import sem.commons.Token;

public class NodeNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NodeNotFoundException(Token token) {
        super(token.toString());
    }
}
