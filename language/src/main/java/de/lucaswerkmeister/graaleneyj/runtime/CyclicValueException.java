package de.lucaswerkmeister.graaleneyj.runtime;

import de.lucaswerkmeister.graaleneyj.nodes.ZTypeCheckNode;

/**
 * Exception thrown by {@link ZReference} to indicate that a reference cycle was
 * detected while evaluating a reference. {@link ZTypeCheckNode} catches this
 * exception and takes it as a sign to skip a type check, since the type of a
 * value can’t be checked if it’s currently being created.
 */
public class CyclicValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CyclicValueException() {
	}

	public CyclicValueException(String message) {
		super(message);
	}

	public CyclicValueException(Throwable cause) {
		super(cause);
	}

	public CyclicValueException(String message, Throwable cause) {
		super(message, cause);
	}

}
