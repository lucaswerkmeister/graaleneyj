package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * A boxed character (Unicode code point). Unboxed characters are represented by
 * {@code int}. For interop, this behaves like a string.
 */
@ExportLibrary(InteropLibrary.class)
public class ZCharacter implements TruffleObject {

	private final int codepoint;

	public ZCharacter(int codepoint) {
		this.codepoint = codepoint;
	}

	@ExportMessage
	public final boolean isString() {
		return true;
	}

	@ExportMessage
	public final String asString() {
		return Character.toString(codepoint);
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
		return asString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ZCharacter)) {
			return false;
		}
		return codepoint == ((ZCharacter) obj).codepoint;
	}

}
