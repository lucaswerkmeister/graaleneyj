package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * The Z23 nothing value. For interop, this acts as the null value.
 */
@ExportLibrary(InteropLibrary.class)
public class ZNothing implements TruffleObject {

	public static final ZNothing INSTANCE = new ZNothing();

	private ZNothing() {
	}

	@ExportMessage
	public final boolean isNull() {
		return true;
	}

	@ExportMessage
	public final boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	public final Class<? extends TruffleLanguage<?>> getLanguage() {
		return ZLanguage.class;
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return "nothing"; // TODO language-dependent label?
	}

}
