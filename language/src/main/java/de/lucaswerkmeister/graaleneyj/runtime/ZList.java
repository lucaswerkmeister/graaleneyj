package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * An object that behaves like a list in other languages: the Z10 list
 * constructor (with head and tail), or the Z13 nil value.
 *
 * I originally tried having this as an abstract class, with subclasses for the
 * cons and nil case, but that crashed javac when the nil subclass had its own
 * export annotations (for behaving like null in interop). Combining both into
 * one class is how Mumbler did it, too.
 */
@ExportLibrary(InteropLibrary.class)
public final class ZList implements TruffleObject {

	private final Object head;
	private final ZList tail;
	private final long length; // cached for efficiency

	public static final ZList NIL = new ZList();

	private ZList() {
		head = null;
		tail = null;
		length = 0;
	}

	public ZList(Object head, ZList tail) {
		assert head != null; // TODO throw instead of assert?
		assert tail != null; // TODO throw instead of assert?
		this.head = head;
		this.tail = tail;
		this.length = 1 + tail.length;
	}

	public Object getHead() {
		assert this != NIL;
		return head;
	}

	public ZList getTail() {
		assert this != NIL;
		return tail;
	}

	@ExportMessage
	public final boolean hasArrayElements() {
		return true;
	}

	@ExportMessage
	public final Object readArrayElement(long index) throws InvalidArrayIndexException {
		if (!isArrayElementReadable(index)) {
			throw InvalidArrayIndexException.create(index);
		}
		if (index == 0) {
			return head;
		} else {
			return tail.readArrayElement(index - 1);
		}
	}

	@ExportMessage
	public final long getArraySize() {
		return length;
	}

	@ExportMessage
	public final boolean isArrayElementReadable(long index) {
		return index >= 0 && index < getArraySize();
	}

	@ExportMessage
	public final boolean hasMembers() {
		return true;
	}

	@ExportMessage
	public final ZListKeys getMembers(boolean includeInternal) {
		return new ZListKeys();
	}

	@ExportMessage
	public final boolean isMemberReadable(String member) {
		return ZConstants.ZOBJECT_TYPE.equals(member) || ZConstants.LIST_HEAD.equals(member)
				|| ZConstants.LIST_TAIL.equals(member);
	}

	@ExportMessage
	public final Object readMember(String member, @CachedContext(ZLanguage.class) ZContext context)
			throws UnknownIdentifierException {
		switch (member) {
		case ZConstants.ZOBJECT_TYPE:
			return new ZReference(ZConstants.LIST, context);
		case ZConstants.LIST_HEAD:
			if (this == NIL) {
				return new ZReference(ZConstants.LISTISNIL, context);
			} else {
				return head;
			}
		case ZConstants.LIST_TAIL:
			if (this == NIL) {
				return new ZReference(ZConstants.LISTISNIL, context);
			} else {
				return tail;
			}
		}
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
	@TruffleBoundary
	public final String toDisplayString(boolean allowSideEffects, @CachedLibrary(limit = "0") InteropLibrary interops) {
		if (this == NIL) {
			return "[]";
		} else if (tail == NIL) {
			return "[" + interops.toDisplayString(head, allowSideEffects) + "]";
		} else {
			return "[" + interops.toDisplayString(head, allowSideEffects) + ", "
					+ tail.toDisplayString(allowSideEffects, interops).substring(1);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ZList)) {
			return false;
		}
		if (this == NIL || obj == NIL) {
			return this == obj;
		}
		ZList list = (ZList) obj;
		return length == list.length && head.equals(list.head) && tail.equals(list.tail);
	}

	/**
	 * Helper object for {@link ZList#getMembers()}. All lists have exactly the
	 * members Z1K1/type, Z10K1/head, and Z10K2/tail.
	 */
	@ExportLibrary(InteropLibrary.class)
	static final class ZListKeys implements TruffleObject {

		@ExportMessage
		public boolean hasArrayElements() {
			return true;
		}

		@ExportMessage
		public boolean isArrayElementReadable(long index) {
			return 0 <= index && index < 3;
		}

		@ExportMessage
		public long getArraySize() {
			return 3;
		}

		@ExportMessage
		public String readArrayElement(long index) throws InvalidArrayIndexException {
			if (0 <= index && index < 3) {
				switch ((int) index) {
				case 0:
					return ZConstants.ZOBJECT_TYPE;
				case 1:
					return ZConstants.LIST_HEAD;
				case 2:
					return ZConstants.LIST_TAIL;
				}
			}
			CompilerDirectives.transferToInterpreter();
			throw InvalidArrayIndexException.create(index);
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
			return "ZListKeys";
		}
	}

}
