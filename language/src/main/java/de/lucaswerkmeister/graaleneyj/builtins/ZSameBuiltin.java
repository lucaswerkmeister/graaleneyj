package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

/**
 * The Z33/same builtin, implemented using {@link Object#equals(Object) equals}.
 * (Most of the real “implementation” is therefore in the equals implementations
 * of our runtime objects.
 *
 * The builtin is supposed to call the Z36/value builtin on its arguments before
 * comparing them. This node does not do that – instead, the parser instantiates
 * the builtin in such a way that its
 * {@link de.lucaswerkmeister.graaleneyj.nodes.ZReadArgumentNode
 * ZReadArgumentNodes} are wrapped in
 * {@link de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltin ZValueBuiltin}
 * nodes. If you construct ASTs using this node in some other way, you may not
 * get the correct behavior.
 */
@NodeInfo(shortName = "same")
public abstract class ZSameBuiltin extends ZBuiltinNode {

	@Specialization
	public boolean sameBoolean(boolean left, boolean right) {
		return left == right;
	}

	@Specialization
	public boolean same(Object left, Object right) {
		return left.equals(right);
	}

}
