package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;

public class ZImplementationFunctioncallNode extends ZImplementationNode {

	@Child
	private ZRootNode node;

	public ZImplementationFunctioncallNode(ZRootNode node, String functionId) {
		super(functionId);
		this.node = node;
	}

	@Override
	public CallTarget makeCallTarget() {
		return Truffle.getRuntime().createCallTarget(node);
	}

}
