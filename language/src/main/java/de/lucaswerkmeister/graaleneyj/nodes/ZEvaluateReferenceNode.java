package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to evaluate a reference until reaching a non-reference value.
 */
public abstract class ZEvaluateReferenceNode extends Node {

	public abstract Object execute(Object value);

	@Specialization
	public Object doReference(ZReference value) {
		Object resolved = value;
		do {
			resolved = ((ZReference) value).evaluate();
		} while (resolved instanceof ZReference);
		return resolved;
	}

	@Fallback
	public Object doOther(Object value) {
		return value;
	}

}
