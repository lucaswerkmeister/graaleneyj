package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

/**
 * Node for an “if” conditional. Conceptually, this is a function call like any
 * other; it’s represented by a special node type for two reasons:
 * <ul>
 * <li>to enable lazy evaluation of only one branch, because (as of this
 * writing) regular function calls are always eagerly evaluated; and</li>
 * <li>to profile the condition for GraalVM.</li>
 * </ul>
 */
public class ZIfNode extends ZNode {

	@Child
	private ZNode condition;

	@Child
	private ZNode consequent;

	@Child
	private ZNode alternative;

	@CompilationFinal
	private ConditionProfile profile = ConditionProfile.createCountingProfile();

	public ZIfNode(ZNode condition, ZNode consequent, ZNode alternative) {
		this.condition = condition;
		this.consequent = consequent;
		this.alternative = alternative;
	}

	@Override
	public ZIfNode copy() {
		ZIfNode zIfNode = (ZIfNode) super.copy();
		zIfNode.profile = ConditionProfile.createCountingProfile();
		return zIfNode;
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		if (profile.profile(evaluateCondition(virtualFrame))) {
			return consequent.execute(virtualFrame);
		} else {
			return alternative.execute(virtualFrame);
		}
	}

	private boolean evaluateCondition(VirtualFrame virtualFrame) {
		try {
			return condition.executeBoolean(virtualFrame);
		} catch (UnexpectedResultException e) {
			// TODO proper error handling when "if" is not called with a boolean
			throw new RuntimeException(e);
		}
	}

}
