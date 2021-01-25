package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.interop.ExceptionType;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;

/**
 * An exception wrapping an uncaught eneyj error object.
 */
@ExportLibrary(value = InteropLibrary.class, delegateTo = "error")
public class ZErrorException extends AbstractTruffleException {

	private static final long serialVersionUID = 1L;
	protected final TruffleObject error;

	public ZErrorException(TruffleObject error, Node location) {
		super(location);
		// TODO assert error != null;
		this.error = error;
	}

	@ExportMessage
	public ExceptionType getExceptionType() {
		return ExceptionType.RUNTIME_ERROR;
	}

	@ExportMessage
	public boolean isExceptionIncompleteSource() {
		return false;
	}

}
