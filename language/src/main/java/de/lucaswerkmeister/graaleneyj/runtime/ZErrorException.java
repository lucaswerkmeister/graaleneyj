package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

/**
 * An exception wrapping an uncaught eneyj error object.
 */
public class ZErrorException extends RuntimeException implements TruffleException {

	private static final long serialVersionUID = 1L;
	private final TruffleObject error;
	private final Node location;

	public ZErrorException(TruffleObject error, Node location) {
		// TODO assert error != null;
		this.error = error;
		this.location = location;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

	@Override
	public Object getExceptionObject() {
		return error;
	}

	@Override
	public Node getLocation() {
		return location;
	}

}
