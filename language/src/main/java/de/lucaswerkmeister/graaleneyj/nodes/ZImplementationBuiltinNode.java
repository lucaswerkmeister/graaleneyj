package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;

public class ZImplementationBuiltinNode extends ZImplementationNode {

	@Child
	private ZRootNode rootNode;

	@CompilationFinal
	private RootCallTarget callTarget = null;

	public ZImplementationBuiltinNode(ZRootNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public RootCallTarget getCallTarget() {
		if (callTarget == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			callTarget = Truffle.getRuntime().createCallTarget(rootNode);
		}
		return callTarget;
	}

}
