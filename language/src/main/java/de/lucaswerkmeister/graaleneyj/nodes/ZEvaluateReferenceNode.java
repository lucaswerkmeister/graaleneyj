package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.runtime.ZPersistentObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to evaluate a reference until reaching a non-reference value.
 *
 * @see ZResolveValueNode
 */
public abstract class ZEvaluateReferenceNode extends Node {

	public abstract Object execute(Object value);

	@Specialization(guards = { "value.equals(cachedValue)" }, limit = "1")
	public Object doReferenceCached(ZReference value, @Cached("value") ZReference cachedValue,
			@CachedLibrary("value") InteropLibrary values, @Cached("doReference(value, values)") Object cachedResult) {
		return cachedResult;
	}

	@Specialization(replaces = "doReferenceCached", limit = "3")
	public Object doReference(ZReference value, @CachedLibrary(value = "value") InteropLibrary values) {
		try {
			Object resolved = values.execute(value);
			assert resolved instanceof ZPersistentObject
					: "Reference must point to persistent object (otherwise we might need to evaluate it repeatedly)";
			return resolved;
		} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
			throw new RuntimeException(e);
		}
	}

	@Fallback
	public Object doOther(Object value) {
		return value;
	}

}
