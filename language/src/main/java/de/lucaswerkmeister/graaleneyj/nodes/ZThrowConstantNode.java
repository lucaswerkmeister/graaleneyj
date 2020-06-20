package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * Internal node type used to create a {@link CallTarget} that immediately
 * throws an exception.
 */
public class ZThrowConstantNode extends ZNode {

	private final RuntimeException exception;

	public ZThrowConstantNode(RuntimeException exception) {
		this.exception = exception;
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		throw exception;
	}

}
