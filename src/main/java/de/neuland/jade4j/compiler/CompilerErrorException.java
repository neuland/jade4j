package de.neuland.jade4j.compiler;

import de.neuland.jade4j.parser.node.ErrorNode;

public class CompilerErrorException extends Exception {

    private static final long serialVersionUID = -4649766458564259961L;

    public CompilerErrorException(ErrorNode errorNode) {
    }

}
