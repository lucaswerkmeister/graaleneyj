package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.runtime.CyclicValueException;

/**
 * <p>
 * Helper node to check the type of a newly created object.
 * </p>
 * <p>
 * Nodes that create objects should pass them through this node before releasing
 * them to the rest of the program. The object will be returned unchanged,
 * unless it is not well-typed, in which case an error is raised.
 * </p>
 */
public abstract class ZTypeCheckNode extends Node {

	public abstract Object execute(Object value);

	@Specialization(guards = { "hasUsableMetaObject(value, values)" }, limit = "1")
	public Object checkHasType(Object value, @CachedLibrary("value") InteropLibrary values) {
		// TODO implement type check
		return value;
	}

	@Fallback
	public Object doNothing(Object value) {
		return value;
	}

	protected boolean hasUsableMetaObject(Object value, InteropLibrary values) {
		try {
			return values.hasMetaObject(value);
		} catch (CyclicValueException e) {
			return false;
		}
	}

}
