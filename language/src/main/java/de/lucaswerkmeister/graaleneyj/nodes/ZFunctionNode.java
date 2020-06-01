package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

import de.lucaswerkmeister.graaleneyj.runtime.ZFunction;
import de.lucaswerkmeister.graaleneyj.runtime.ZImplementation;

public class ZFunctionNode extends ZNode {

	@Children
	private final ZImplementationNode[] implementations;

	public ZFunctionNode(ZImplementationNode implementations[]) {
		this.implementations = implementations;
	}

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame virtualFrame) {
		CompilerAsserts.compilationConstant(implementations.length);
		ZImplementation[] impls = new ZImplementation[implementations.length];
		for (int i = 0; i < implementations.length; i++) {
			impls[i] = (ZImplementation) implementations[i].execute(virtualFrame);
		}
		return new ZFunction(impls);
	}

}
