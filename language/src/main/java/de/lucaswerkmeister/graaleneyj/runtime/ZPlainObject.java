package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.LibraryFactory;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.nodes.ZPairNode;

/**
 * <p>
 * A generic object with members, initialized at construction time.
 * </p>
 * <p>
 * AST nodes may add additional members to the object after it has been created,
 * e. g. to use a more specific/optimized {@link DynamicObjectLibrary} (compare
 * {@link ZPairNode}). However, objects are supposed to be immutable – nodes
 * should therefore only add members to objects immediately after their
 * creation, before they are released to other parts of the program.
 * </p>
 */
@ExportLibrary(InteropLibrary.class)
public class ZPlainObject extends ZObject {

	public ZPlainObject(Shape shape) {
		super(shape);
	}

	public ZPlainObject(Shape shape, Map<String, Object> members) {
		this(shape);
		DynamicObjectLibrary objects = DynamicObjectLibrary.getUncached();
		for (Map.Entry<String, Object> entry : members.entrySet()) {
			objects.put(this, entry.getKey(), entry.getValue());
		}
	}

	/**
	 * {@link ZPlainObject} values are seem as objects with members by other
	 * languages.
	 */
	@ExportMessage
	public boolean hasMembers() {
		return true;
	}

	/**
	 * {@link ZPlainObject} values are seen as objects with the IDs of their keys by
	 * other languages. That is, other languages always see keys like “Z10K1”, not
	 * “head”.
	 *
	 * @param booleanInternal Ignored, we have no internal keys.
	 */
	@ExportMessage
	public ZPlainObjectKeys getMembers(boolean includeInternal,
			@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return new ZPlainObjectKeys(objectLibrary.getKeyArray(this));
	}

	/**
	 * {@link ZPlainObject} members are readable if a key of that ID exists.
	 */
	@ExportMessage
	public boolean isMemberReadable(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
		return objectLibrary.containsKey(this, member);
	}

	/**
	 * {@link ZPlainObject} members are read by key ID.
	 */
	@ExportMessage
	public Object readMember(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary)
			throws UnknownIdentifierException {
		Object value = objectLibrary.getOrDefault(this, member, null);
		if (value != null) {
			return value;
		}
		throw UnknownIdentifierException.create(member);
	}

	// no write-related methods are exported, objects are immutable

	@ExportMessage
	@TruffleBoundary
	public final String toDisplayString(boolean allowSideEffects,
			@CachedLibrary("this") DynamicObjectLibrary objectLibrary,
			@CachedLibrary(limit = "0") InteropLibrary interops) {
		StringBuilder ret = new StringBuilder("{");
		Object[] keys = objectLibrary.getKeyArray(this);
		for (int i = 0; i < keys.length; i++) {
			if (i != 0) {
				ret.append(", ");
			}
			ret.append('"').append(keys[i]).append("\": ");
			ret.append(interops.toDisplayString(objectLibrary.getOrDefault(this, keys[i], null), allowSideEffects));
		}
		ret.append("}");
		return ret.toString();
	}

	/**
	 * A ZPlainObject is equal to another object if that object has the same
	 * members:
	 * <ol>
	 * <li>the other object has members at all;</li>
	 * <li>this and that object’s members lists have the same size;</li>
	 * <li>each member of this object is readable in that object;</li>
	 * <li>this and that object’s values for the member are equal.</li>
	 * </ol>
	 */
	@Override
	public boolean equals(Object that) {
		try {
			final LibraryFactory<InteropLibrary> factory = InteropLibrary.getFactory();
			InteropLibrary thisLibrary = factory.getUncached(this);
			InteropLibrary thatLibrary = factory.getUncached(that);
			if (!thatLibrary.hasMembers(that)) {
				return false;
			}
			Object thisMembers = thisLibrary.getMembers(this);
			InteropLibrary thisMembersLibrary = factory.getUncached(thisMembers);
			Object thatMembers = thatLibrary.getMembers(that);
			InteropLibrary thatMembersLibrary = factory.getUncached(thatMembers);
			if (thisMembersLibrary.getArraySize(thisMembers) != thatMembersLibrary.getArraySize(thatMembers)) {
				return false;
			}
			for (long i = 0; i < thisMembersLibrary.getArraySize(thisMembers); i++) {
				String member = (String) thisMembersLibrary.readArrayElement(thisMembers, i);
				Object thisMember = thisLibrary.readMember(this, member);
				if (!thatLibrary.isMemberReadable(that, member)) {
					return false;
				}
				Object thatMember = thatLibrary.readMember(that, member);
				if (!thisMember.equals(thatMember)) {
					return false;
				}
			}
			return true;
		} catch (UnsupportedMessageException | UnknownIdentifierException | InvalidArrayIndexException e) {
			// this should never happen, we check all the necessary conditions
			throw new RuntimeException(e);
		}
	}

	@ExportLibrary(InteropLibrary.class)
	static final class ZPlainObjectKeys implements TruffleObject {

		final Object[] keys;

		public ZPlainObjectKeys(Object[] keys) {
			this.keys = keys;
		}

		@ExportMessage
		public boolean hasArrayElements() {
			return true;
		}

		@ExportMessage
		public boolean isArrayElementReadable(long index) {
			return 0 <= index && index < keys.length;
		}

		@ExportMessage
		public long getArraySize() {
			return keys.length;
		}

		@ExportMessage
		public Object readArrayElement(long index) throws InvalidArrayIndexException {
			if (!isArrayElementReadable(index)) {
				CompilerDirectives.transferToInterpreter();
				throw InvalidArrayIndexException.create(index);
			}
			return keys[(int) index];
		}

	}

}
