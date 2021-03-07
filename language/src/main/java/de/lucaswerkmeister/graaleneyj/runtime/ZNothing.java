package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.library.ZTypeIdentityLibrary;

/**
 * The Z23 nothing value. For interop, this acts as the null value.
 */
@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ZNothing extends ZObject {

	public static final ZNothing INSTANCE = new ZNothing();

	private ZNothing() {
		super(STATIC_BLANK_SHAPE);
	}

	@ExportMessage
	public String getTypeIdentity() {
		// According to AbstractText/eneyj, Z23 is its own instance.
		// However, that would require it to be a meta object,
		// and Truffle does not permit a value to be null and a meta object
		// simultaneously.
		return ZConstants.TYPE;
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
