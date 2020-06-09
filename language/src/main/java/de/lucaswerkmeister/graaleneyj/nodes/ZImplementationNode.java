package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.runtime.ZImplementation;

public abstract class ZImplementationNode extends ZNode {

	public abstract CallTarget getCallTarget();

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return new ZImplementation(getCallTarget());
	}

}
