package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.Map;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to create a Z2/pair.
 */
public abstract class ZPairNode extends Node {

	public abstract Object execute(Object first, Object second);

	@Specialization
	public Object doGeneric(Object first, Object second, @CachedContext(ZLanguage.class) ZContext context) {
		return context.makeObject(Map.<String, Object>of( //
				ZConstants.ZOBJECT_TYPE, new ZReference(ZConstants.PAIR, context), //
				ZConstants.PAIR_FIRST, first, //
				ZConstants.PAIR_SECOND, second //
		));
	}

}
