package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZConstants;
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

	public static ZCharacter cast(int codepoint) {
		return new ZCharacter(codepoint);
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
	public final boolean hasMembers() {
		return true;
	}

	@ExportMessage
	public final ZCharacterKeys getMembers(boolean includeInternal) {
		// TODO custom members
		return new ZCharacterKeys(new String[0]);
	}

	@ExportMessage
	public final boolean isMemberReadable(String member) {
		// TODO custom members
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.CHARACTER_CHARACTER.equals(member);
	}

	@ExportMessage
	public final Object readMember(String member) throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return ZConstants.CHARACTER;
		case ZConstants.CHARACTER_CHARACTER:
			return asString();
		}
		// TODO custom members
		throw UnknownIdentifierException.create(member);
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

	@ExportLibrary(InteropLibrary.class)
	static final class ZCharacterKeys implements TruffleObject {

		private final String[] extraKeys;

		public ZCharacterKeys(String[] extraKeys) {
			this.extraKeys = extraKeys;
		}

		@ExportMessage
		public boolean hasArrayElements() {
			return true;
		}

		@ExportMessage
		public boolean isArrayElementReadable(long index) {
			return 0 <= index && index < extraKeys.length + 2;
		}

		@ExportMessage
		public long getArraySize() {
			return extraKeys.length + 2;
		}

		@ExportMessage
		public String readArrayElement(long index) throws InvalidArrayIndexException {
			if (!isArrayElementReadable(index)) {
				CompilerDirectives.transferToInterpreter();
				throw InvalidArrayIndexException.create(index);
			}
			if (index < extraKeys.length) {
				return extraKeys[(int) index];
			}
			if (index == extraKeys.length) {
				return ZConstants.ZOBJECT_TYPE;
			} else {
				return ZConstants.CHARACTER_CHARACTER;
			}
		}

		@ExportMessage
		public boolean hasLanguage() {
			return true;
		}

		@ExportMessage
		public Class<? extends TruffleLanguage<?>> getLanguage() {
			return ZLanguage.class;
		}

		@ExportMessage
		public final String toDisplayString(boolean allowSideEffects) {
			return "ZCharacterKeys";
		}
	}

}
