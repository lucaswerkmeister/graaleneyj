package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.runtime.ZImplementation;

public abstract class ZImplementationNode extends ZNode {

	public abstract RootCallTarget getCallTarget();

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return new ZImplementation(getCallTarget());
	}

}
