package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.Map;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to create a Z22/pair.
 */
public abstract class ZPairNode extends Node {

	public abstract Object execute(Object first, Object second);

	@Specialization
	public Object doGeneric(Object first, Object second, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "3") DynamicObjectLibrary putType,
			@CachedLibrary(limit = "3") DynamicObjectLibrary putFirst,
			@CachedLibrary(limit = "3") DynamicObjectLibrary putSecond) {
		DynamicObject pair = context.makePlainObject(Map.of());
		putType.put(pair, ZConstants.ZOBJECT_TYPE, new ZReference(ZConstants.PAIR));
		putFirst.put(pair, ZConstants.PAIR_FIRST, first);
		putSecond.put(pair, ZConstants.PAIR_SECOND, second);
		return pair;
	}

}
