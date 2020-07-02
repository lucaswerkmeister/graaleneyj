package de.lucaswerkmeister.graaleneyj.runtime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * An unevaluated reference, exposed to other languages as a 0-adic function;
 * calling the function evaluates the reference.
 */
@ExportLibrary(InteropLibrary.class)
public class ZReference implements TruffleObject {

	private final String id;
	private final ZContext context;
	private static final Map<String, Object> registry = new HashMap<>();

	public ZReference(String id, ZContext context) {
		this.id = id;
		this.context = context;
	}

	public String getId() {
		return id;
	}

	@ExportMessage
	public boolean isExecutable() {
		return true;
	}

	@ExportMessage
	public Object execute(Object... arguments) throws ArityException {
		if (arguments.length > 0) {
			throw ArityException.create(0, arguments.length);
		}
		return evaluate();
	}

	public Object evaluate() {
		// TODO for now, global scope is all we have
		if (registry.containsKey(id)) {
			return registry.get(id);
		} else {
			CompilerDirectives.transferToInterpreter();
			Source source;
			try {
				source = Source.newBuilder(ZLanguage.ID, context.getTruffleFile(id)).build();
				CallTarget callTarget = context.parse(source);
				Object value = callTarget.call();
				registry.put(id, value);
				return value;
			} catch (IOException e) {
				throw new RuntimeException(e); // TODO better error handling
			}
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
		return id;
	}

}
