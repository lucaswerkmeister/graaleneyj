package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.library.ZTypeIdentityLibrary;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNode;

/**
 * <p>
 * The base class of all objects in our language.
 * <p>
 * <p>
 * Subclasses must at least export {@link ZTypeIdentityLibrary#getTypeIdentity}
 * and {@link InteropLibrary#toDisplayString}.
 * </p>
 * <p>
 * All ZObjects extend {@link DynamicObject}. However, objects are meant to be
 * immutable, and not all are meant to have any dynamic members. Consequently,
 * the only place where it is allowed to add dynamic members to a ZObject (using
 * a {@link DynamicObjectLibrary}) is immediately after the object was created
 * using a {@link Shape} taken from the {@link ZContext context}. Subclasses
 * that are not meant to have dynamic members should not have a constructor that
 * takes a {@link Shape} argument, and should instead pass the
 * {@link #STATIC_BLANK_SHAPE} into the parent constructor; this signals to
 * users of the class that they are not allowed to add dynamic members even if
 * they created the object, because they did not pass a {@link Shape} into the
 * constructor.
 * </p>
 *
 * @see ZPlainObject
 */
@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public abstract class ZObject extends DynamicObject implements TruffleObject {

	/**
	 * A shape for subclasses that are not meant to have dynamic members.
	 */
	protected static final Shape STATIC_BLANK_SHAPE = Shape.newBuilder().build();

	public ZObject(Shape shape) {
		super(shape);
	}

	@ExportMessage
	public final boolean hasTypeIdentity() {
		return true;
	}

	@ExportMessage
	public String getTypeIdentity() {
		throw new IllegalStateException(
				"Subclass did not override getTypeIdentity(): " + this.getClass().getCanonicalName());
	}

	@ExportMessage
	public final boolean hasMetaObject() {
		return true;
	}

	@ExportMessage
	public final Object getMetaObject(@CachedLibrary("this") ZTypeIdentityLibrary objects,
			@Cached("create()") ZEvaluateReferenceNode evaluateReference) {
		String typeIdentity = objects.getTypeIdentity(this);
		ZReference typeReference = new ZReference(typeIdentity);
		Object type = evaluateReference.execute(typeReference);
		return type;
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
