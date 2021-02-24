package de.lucaswerkmeister.graaleneyj.runtime;

import java.io.IOException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * An unevaluated reference, exposed to other languages as a 0-adic function;
 * calling the function evaluates the reference.
 */
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
	public boolean isExecutable() {
		return true;
	}

	@ExportMessage
	public abstract static class Execute {
		// TODO The cachedId guard *might* be unnecessary; try removing it when we have
		// a lot more tests :)
		@Specialization(guards = { "arguments.length == 0", "reference.id.equals(cachedId)" })
		protected static Object cached(ZReference reference, Object[] arguments,
				@CachedContext(ZLanguage.class) ZContext context, @Cached("reference.id") String cachedId,
				@Cached(value = "generic(reference, arguments, context)", allowUncached = true) Object result) {
			return result;
		}

		@Specialization(guards = { "arguments.length == 0" })
		protected static Object generic(ZReference reference, Object[] arguments,
				@CachedContext(ZLanguage.class) ZContext context) {
			if (context.hasObject(reference.id)) {
				return context.getObject(reference.id);
			}

			CompilerDirectives.transferToInterpreterAndInvalidate();
			Source source;
			try {
				source = Source.newBuilder(ZLanguage.ID, context.getTruffleFile(reference.id)).build();
				CallTarget callTarget = context.parse(source);
				Object value = callTarget.call();
				context.putObject(reference.id, value);
				return value;
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
