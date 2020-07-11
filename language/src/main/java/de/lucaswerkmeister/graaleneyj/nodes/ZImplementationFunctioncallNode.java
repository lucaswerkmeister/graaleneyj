package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;

public class ZImplementationFunctioncallNode extends ZImplementationNode {

	@Child
	private ZRootNode node;

	@CompilationFinal
	private CallTarget callTarget = null;

	public ZImplementationFunctioncallNode(ZRootNode node, String functionId) {
		super(functionId);
		this.node = node;
	}

	@Override
	public CallTarget getCallTarget() {
		if (callTarget == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			callTarget = Truffle.getRuntime().createCallTarget(node);
		}
		return callTarget;
	}

}
