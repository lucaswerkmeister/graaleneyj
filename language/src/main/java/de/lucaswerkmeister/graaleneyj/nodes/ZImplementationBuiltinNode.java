package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;

public class ZImplementationBuiltinNode extends ZImplementationNode {

	@Child
	private ZRootNode rootNode;

	@CompilationFinal
	private CallTarget callTarget = null;

	public ZImplementationBuiltinNode(ZRootNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public CallTarget getCallTarget() {
		if (callTarget == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			callTarget = Truffle.getRuntime().createCallTarget(rootNode);
		}
		return callTarget;
	}

}
