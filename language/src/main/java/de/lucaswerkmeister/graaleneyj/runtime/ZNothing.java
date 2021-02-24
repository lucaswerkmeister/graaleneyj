package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * The Z23 nothing value. For interop, this acts as the null value.
 */
@ExportLibrary(InteropLibrary.class)
public class ZNothing extends ZObject {

	public static final ZNothing INSTANCE = new ZNothing();

	private ZNothing() {
		super(STATIC_BLANK_SHAPE);
	}

	@ExportMessage
	public final boolean isNull() {
		return true;
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return "nothing"; // TODO language-dependent label?
	}

}
