package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZPairNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZPairNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZReifyNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReifyNodeGen;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * <p>
 * The Z37/reify builtin, turning an object into a list of key / reified value
 * pairs. The order of the returned list is unspecified.
 * </p>
 * TODO:
 * <ul>
 * <li>Handle other specially handled types – Z50/boolean, Z23/nothing, etc.
 * </ul>
 */
@NodeInfo(shortName = "reify")
public abstract class ZReifyBuiltin extends ZBuiltinNode {

	@Child
	private ZEvaluateReferenceNode evaluateReference = ZEvaluateReferenceNodeGen.create();

	@Child
	private ZReifyNode reify = ZReifyNodeGen.create();

	@Child
	private ZPairNode pair = ZPairNodeGen.create();

	@Specialization
	public ZList doString(String value, @CachedContext(ZLanguage.class) ZContext context) {
		return new ZList(pair.execute(ZConstants.ZOBJECT_TYPE, new ZReference(ZConstants.STRING, context)),
				new ZList(pair.execute(ZConstants.STRING_STRING_VALUE, value), ZList.NIL));
	}

	@Fallback
	public Object doGeneric(Object value) {
		value = evaluateReference.execute(value);
		return reify.execute(value);
	}

}
