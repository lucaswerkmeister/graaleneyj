package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.library.ZTypeIdentityLibrary;

/**
 * A boxed character (Unicode code point). Unboxed characters are represented by
 * {@code int}. For interop, this behaves like a string.
 */
@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ZCharacter extends ZObject {

	private final int codepoint;

	public ZCharacter(int codepoint, Shape shape) {
		super(shape);
		this.codepoint = codepoint;
	}

	public static ZCharacter cast(int codepoint) {
		return new ZCharacter(codepoint, STATIC_BLANK_SHAPE);
	}

	public int getCodepoint() {
		return codepoint;
	}

	@ExportMessage
	public String getTypeIdentity() {
		return ZConstants.CHARACTER;
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
	public final ZCharacterKeys getMembers(boolean includeInternal,
			@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return new ZCharacterKeys(objectLibrary.getKeyArray(this));
	}

	@ExportMessage
	public final boolean isMemberReadable(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.CHARACTER_CHARACTER.equals(member)
				|| objectLibrary.containsKey(this, member);
	}

	@ExportMessage
	public final Object readMember(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary)
			throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return new ZReference(ZConstants.CHARACTER);
		case ZConstants.CHARACTER_CHARACTER:
			return asString();
		}
		Object value = objectLibrary.getOrDefault(this, member, null);
		if (value != null) {
			return value;
		} else {
			throw UnknownIdentifierException.create(member);
		}
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

		private final Object[] extraKeys;

		public ZCharacterKeys(Object[] extraKeys) {
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
		public Object readArrayElement(long index) throws InvalidArrayIndexException {
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
	}

}
