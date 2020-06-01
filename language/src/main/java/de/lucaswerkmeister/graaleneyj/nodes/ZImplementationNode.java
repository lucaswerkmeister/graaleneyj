package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.runtime.ZImplementation;

public class ZImplementationNode extends ZNode {

	private final RootCallTarget callTarget;

	public ZImplementationNode(RootCallTarget callTarget) {
		this.callTarget = callTarget;
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return new ZImplementation(callTarget);
	}

}
