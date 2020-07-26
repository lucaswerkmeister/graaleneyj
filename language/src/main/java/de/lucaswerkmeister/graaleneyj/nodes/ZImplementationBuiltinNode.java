package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

public class ZImplementationBuiltinNode extends ZImplementationNode {

	@Child
	private ZRootNode rootNode;

	public ZImplementationBuiltinNode(ZRootNode rootNode, String functionId) {
		super(functionId);
		this.rootNode = rootNode;
	}

	@Override
	public CallTarget makeCallTarget() {
		return Truffle.getRuntime().createCallTarget(rootNode);
	}

}
