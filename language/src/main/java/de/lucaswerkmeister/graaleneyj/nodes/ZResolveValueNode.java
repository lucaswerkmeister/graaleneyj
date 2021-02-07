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
 * Helper node to evaluate references and unpack persistent objects until
 * reaching a value that is neither a reference nor a persistent object.
 * 
 * @see ZEvaluateReferenceNode
 */
public abstract class ZResolveValueNode extends Node {

	public abstract Object execute(Object value);

	@Specialization(guards = { "value.equals(cachedValue)" }, limit = "1")
	public Object doReferenceCached(ZReference value, @Cached("value") ZReference cachedValue,
			@CachedLibrary("value") InteropLibrary values, @Cached("doReference(value, values)") Object cachedResult) {
		return cachedResult;
	}

	@Specialization(replaces = "doReferenceCached", limit = "3")
	public Object doReference(ZReference value, @CachedLibrary(value = "value") InteropLibrary values) {
		Object resolved = value;
		do {
			try {
				if (resolved instanceof ZReference) {
					resolved = values.execute(resolved);
				} else {
					resolved = ((ZPersistentObject) resolved).getValue();
				}
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
				throw new RuntimeException(e);
			}
		} while (resolved instanceof ZReference || resolved instanceof ZPersistentObject);
		return resolved;
	}

	@Specialization(guards = { "value.equals(cachedValue)" }, limit = "1")
	public Object doPersistentObjectCached(ZPersistentObject value, @Cached("value") ZPersistentObject cachedValue,
			@CachedLibrary("value") InteropLibrary values,
			@Cached("doPersistentObject(value, values)") Object cachedResult) {
		return cachedResult;
	}

	@Specialization(replaces = "doPersistentObjectCached", limit = "3")
	public Object doPersistentObject(ZPersistentObject value, @CachedLibrary(value = "value") InteropLibrary values) {
		Object resolved = value;
		do {
			try {
				if (resolved instanceof ZReference) {
					resolved = values.execute(resolved);
				} else {
					resolved = ((ZPersistentObject) resolved).getValue();
				}
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
				throw new RuntimeException(e);
			}
		} while (resolved instanceof ZReference || resolved instanceof ZPersistentObject);
		return resolved;
	}

	@Fallback
	public Object doOther(Object value) {
		return value;
	}

}
