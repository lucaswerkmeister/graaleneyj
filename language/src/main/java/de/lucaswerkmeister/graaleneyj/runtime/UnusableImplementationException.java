package de.lucaswerkmeister.graaleneyj.runtime;

/**
 * Exception thrown by an implementation to indicate that it cannot be used.
 * This is not recoverable, and the implementation is not expected to be called
 * again.
 */
public class UnusableImplementationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnusableImplementationException() {
	}

	public UnusableImplementationException(String message) {
		super(message);
	}

	public UnusableImplementationException(Throwable cause) {
		super(cause);
	}

	public UnusableImplementationException(String message, Throwable cause) {
		super(message, cause);
	}

}
