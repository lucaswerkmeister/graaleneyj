package de.lucaswerkmeister.graaleneyj.runtime;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.library.ZTypeIdentityLibrary;

/**
 * An unevaluated reference, exposed to other languages as a 0-adic function;
 * calling the function evaluates the reference.
 */
@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ZReference extends ZObject {

	protected final String id;

	public ZReference(String id) {
		super(STATIC_BLANK_SHAPE);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@ExportMessage
	public String getTypeIdentity() {
		return ZConstants.REFERENCE;
	}

	@ExportMessage
	public boolean isExecutable() {
		return true;
	}

	@ExportMessage
	public abstract static class Execute {
		@Specialization(guards = { "arguments.length == 0", "reference.id.equals(cachedId)" }, limit = "1")
		protected static Object cached(ZReference reference, Object[] arguments,
				@CachedContext(ZLanguage.class) ZContext context,
				@CachedLibrary("context.getPersistentObjectRegistry()") DynamicObjectLibrary objects,
				@Cached("reference.id") String cachedId,
				@Cached(value = "generic(reference, arguments, context, objects)", allowUncached = true) Object result) {
			return result;
		}

		@Specialization(guards = { "arguments.length == 0" }, limit = "1")
		protected static Object generic(ZReference reference, Object[] arguments,
				@CachedContext(ZLanguage.class) ZContext context,
				@CachedLibrary("context.getPersistentObjectRegistry()") DynamicObjectLibrary objects) {
			// Check if the value already exists. This is the only part that makes it into
			// compiled code.
			DynamicObject persistentObjectRegistry = context.getPersistentObjectRegistry();
			if (objects.containsKey(persistentObjectRegistry, reference.id)) {
				Object value = objects.getOrDefault(persistentObjectRegistry, reference.id, null);
				if (value != null) {
					return value;
				} else {
					// We were already trying to evaluate the reference, which is why it’s present,
					// but set to {@code null}, in the registry. Since we’re now trying to evaluate
					// it again, there must be a cycle – abort.
					throw new CyclicValueException();
				}
			}

			// We were unable to load an already-evaluated value. This is the slow path.
			CompilerDirectives.transferToInterpreterAndInvalidate();

			// Set the ID to null in the persistent object registry, to mark that we’re now
			// trying to evaluate it, making it possible to detect cycles. Since this is a
			// modification to the registry, it must happen under lock. Additionally,
			// because the earlier check was not guarded by the lock, it’s possible that the
			// ID now exists in the registry, so we also repeat the whole check above.
			Lock persistentObjectRegistryLock = context.getPersistentObjectRegistryLock();
			persistentObjectRegistryLock.lock();
			try {
				if (objects.containsKey(persistentObjectRegistry, reference.id)) {
					Object value = objects.getOrDefault(persistentObjectRegistry, reference.id, null);
					if (value != null) {
						return value;
					} else {
						throw new CyclicValueException();
					}
				}
				objects.put(persistentObjectRegistry, reference.id, null);
			} finally {
				// Release the lock, since the next part (evaluating the value) is slow.
				persistentObjectRegistryLock.unlock();
			}

			// The value really doesn’t exist yet. Load its source and evaluate it.
			Source source;
			try {
				source = Source.newBuilder(ZLanguage.ID, context.getTruffleFile(reference.id)).build();
				CallTarget callTarget = context.parse(source);
				Object value = callTarget.call();
				// We have the value, let’s put it in the persistent object registry.
				persistentObjectRegistryLock.lock();
				try {
					Object storedValue = objects.getOrDefault(persistentObjectRegistry, reference.id, null);
					if (storedValue != null) {
						// Another thread beat us to it. Discard our value.
						value = storedValue;
					} else {
						objects.put(persistentObjectRegistry, reference.id, value);
					}
					return value;
				} finally {
					persistentObjectRegistryLock.unlock();
				}
			} catch (IOException e) {
				throw new RuntimeException(e); // TODO better error handling
			}
		}

		@Specialization(guards = { "arguments.length > 0" })
		protected static Object wrongArity(ZReference reference, Object[] arguments) throws ArityException {
			throw ArityException.create(0, arguments.length);
		}
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ZReference && id.equals(((ZReference) obj).id);
	}

}
