package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
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

@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ZType extends ZObject {

	private final String identity;

	public ZType(String identity, Shape shape) {
		super(shape);
		assert identity != null; // TODO check even with assertions disabled?
		this.identity = identity;
	}

	@ExportMessage
	public String getTypeIdentity() {
		return ZConstants.TYPE;
	}

	@ExportMessage
	public boolean hasMembers() {
		return true;
	}

	@ExportMessage
	public ZTypeKeys getMembers(boolean includeInternal, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return new ZTypeKeys(objectLibrary.getKeyArray(this));
	}

	@ExportMessage
	public boolean isMemberReadable(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.TYPE_IDENTITY.equals(member)
				|| objectLibrary.containsKey(this, member);
	}

	@ExportMessage
	public Object readMember(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary)
			throws UnknownIdentifierException {
		if (ZConstants.ZOBJECT_TYPE.equals(member)) {
			return new ZReference(ZConstants.TYPE);
		}
		if (ZConstants.TYPE_IDENTITY.equals(member)) {
			return new ZReference(identity);
		}
		Object value = objectLibrary.getOrDefault(this, member, null);
		if (value != null) {
			return value;
		}
		throw UnknownIdentifierException.create(member);
	}

	@ExportMessage
	public boolean isMetaObject() {
		return true;
	}

	@ExportMessage
	public String getMetaSimpleName() {
		return identity;
	}

	@ExportMessage
	public String getMetaQualifiedName() {
		return identity;
	}

	@ExportMessage
	public abstract static class IsMetaInstance {
		@Specialization(guards = { "objects.hasTypeIdentity(object)" }, limit = "3")
		public static boolean doTypeIdentity(ZType type, Object object,
				@CachedLibrary("object") ZTypeIdentityLibrary objects) {
			return type.identity.equals(objects.getTypeIdentity(object));
		}

		@Fallback
		public static boolean doOther(ZType type, Object object) {
			return false;
		}
	}

	@ExportMessage
	@Override
	public String toDisplayString(boolean allowSideEffects) {
		return identity;
	}

	@ExportLibrary(InteropLibrary.class)
	static final class ZTypeKeys implements TruffleObject {

		final Object[] keys;

		public ZTypeKeys(Object[] keys) {
			this.keys = keys;
		}

		@ExportMessage
		public boolean hasArrayElements() {
			return true;
		}

		@ExportMessage
		public boolean isArrayElementReadable(long index) {
			return 0 <= index && index < keys.length + 2;
		}

		@ExportMessage
		public long getArraySize() {
			return keys.length + 2;
		}

		@ExportMessage
		public Object readArrayElement(long index) throws InvalidArrayIndexException {
			if (!isArrayElementReadable(index)) {
				CompilerDirectives.transferToInterpreter();
				throw InvalidArrayIndexException.create(index);
			}
			switch ((int) index) {
			case 0:
				return ZConstants.ZOBJECT_TYPE;
			case 1:
				return ZConstants.TYPE_IDENTITY;
			default:
				return keys[(int) index - 2];
			}
		}

	}

}
