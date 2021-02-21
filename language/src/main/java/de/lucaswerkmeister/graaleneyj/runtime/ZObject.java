package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * <p>
 * The base class of all objects in our language.
 * <p>
 * <p>
 * Subclasses must at least export
 * {@link InteropLibrary#toDisplayString(Object, boolean) toDisplayString} and
 * {@link InteropLibrary#hasMembers(Object) hasMembers} with related methods.
 * </p>
 *
 * @see ZPlainObject
 */
@ExportLibrary(InteropLibrary.class)
public abstract class ZObject extends DynamicObject implements TruffleObject {

	public ZObject(Shape shape) {
		super(shape);
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
	public String toDisplayString(boolean allowSideEffects) {
		throw new IllegalStateException(
				"Subclass did not override toDisplayString(): " + this.getClass().getCanonicalName());
	}

}
