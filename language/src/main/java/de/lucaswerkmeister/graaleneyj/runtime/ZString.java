package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.Map;

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
 * A boxed Z6/string. Should only be used for strings that have extra members,
 * “plain” strings are represented by {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class ZString implements TruffleObject {

	private final String value;
	private final Map<String, Object> extraMembers;

	public ZString(String value, Map<String, Object> extraMembers) {
		assert value != null;
		assert !extraMembers.containsKey(ZConstants.ZOBJECT_TYPE);
		assert !extraMembers.containsKey(ZConstants.STRING_STRING_VALUE);
		assert !extraMembers.isEmpty();
		this.value = value;
		this.extraMembers = Map.copyOf(extraMembers);
	}

	@ExportMessage
	public final boolean isString() {
		return true;
	}

	@ExportMessage
	public final String asString() {
		return value;
	}

	@ExportMessage
	public final boolean hasMembers() {
		return true;
	}

	@ExportMessage
	public final ZStringKeys getMembers(boolean includeInternal) {
		return new ZStringKeys(extraMembers.keySet().toArray(new String[extraMembers.size()]));
	}

	@ExportMessage
	public final boolean isMemberReadable(String member) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.STRING_STRING_VALUE.equals(member)
				|| extraMembers.containsKey(member);
	}

	@ExportMessage
	public final Object readMember(String member) throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return new ZReference(ZConstants.STRING);
		case ZConstants.STRING_STRING_VALUE:
			return value;
		}
		if (extraMembers.containsKey(member)) {
			return extraMembers.get(member);
		} else {
			throw UnknownIdentifierException.create(member);
		}
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
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ZString)) {
			return false;
		}
		return value.equals(((ZString) obj).value);
	}

	@ExportLibrary(InteropLibrary.class)
	static final class ZStringKeys implements TruffleObject {

		private final String[] extraKeys;

		public ZStringKeys(String[] extraKeys) {
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
				return ZConstants.STRING_STRING_VALUE;
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
			return "ZStringKeys";
		}
	}

}
