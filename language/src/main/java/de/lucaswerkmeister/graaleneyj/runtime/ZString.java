package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.CachedContext;
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
import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * A boxed Z6/string. Should only be used for strings that have extra members,
 * “plain” strings are represented by {@link String}.
 */
@ExportLibrary(InteropLibrary.class)
public class ZString extends ZObject {

	private final String value;

	public ZString(String value, Shape shape) {
		super(shape);
		assert value != null;
		this.value = value;
	}

	public ZString(String value, Shape shape, Map<String, Object> extraMembers) {
		this(value, shape);
		assert !extraMembers.containsKey(ZConstants.ZOBJECT_TYPE);
		assert !extraMembers.containsKey(ZConstants.STRING_STRING_VALUE);
		assert !extraMembers.isEmpty();
		DynamicObjectLibrary objects = DynamicObjectLibrary.getUncached();
		for (Map.Entry<String, Object> entry : extraMembers.entrySet()) {
			objects.put(this, entry.getKey(), entry.getValue());
		}
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
	public final ZStringKeys getMembers(boolean includeInternal,
			@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return new ZStringKeys(objectLibrary.getKeyArray(this));
	}

	@ExportMessage
	public final boolean isMemberReadable(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.STRING_STRING_VALUE.equals(member)
				|| objectLibrary.containsKey(this, member);
	}

	@ExportMessage
	public final Object readMember(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary,
			@CachedContext(ZLanguage.class) ZContext context) throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return new ZReference(ZConstants.STRING, context.getInitialZObjectShape());
		case ZConstants.STRING_STRING_VALUE:
			return value;
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

		private final Object[] extraKeys;

		public ZStringKeys(Object[] extraKeys) {
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
				return ZConstants.STRING_STRING_VALUE;
			}
		}
	}

}
