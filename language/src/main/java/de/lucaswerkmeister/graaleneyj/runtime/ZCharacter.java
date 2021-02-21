package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.ZConstants;

/**
 * A boxed character (Unicode code point). Unboxed characters are represented by
 * {@code int}. For interop, this behaves like a string.
 */
@ExportLibrary(InteropLibrary.class)
public class ZCharacter extends ZObject {

	private final int codepoint;
	private final Map<String, Object> extraMembers;

	public ZCharacter(int codepoint, Shape shape, Map<String, Object> extraMembers) {
		super(shape);
		assert !extraMembers.containsKey(ZConstants.ZOBJECT_TYPE);
		assert !extraMembers.containsKey(ZConstants.CHARACTER_CHARACTER);
		this.codepoint = codepoint;
		this.extraMembers = Map.copyOf(extraMembers);
	}

	public static ZCharacter cast(int codepoint, Shape shape) {
		return new ZCharacter(codepoint, shape, Map.of());
	}

	public int getCodepoint() {
		return codepoint;
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
		return new ZCharacterKeys(extraMembers.keySet().toArray(new String[extraMembers.size()]));
	}

	@ExportMessage
	public final boolean isMemberReadable(String member) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.CHARACTER_CHARACTER.equals(member)
				|| extraMembers.containsKey(member);
	}

	@ExportMessage
	public final Object readMember(String member) throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return new ZReference(ZConstants.CHARACTER);
		case ZConstants.CHARACTER_CHARACTER:
			return asString();
		}
		if (extraMembers.containsKey(member)) {
			return extraMembers.get(member);
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
	}

}
