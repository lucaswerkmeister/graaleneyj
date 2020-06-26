package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

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

}
