package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.runtime.ZImplementation;

public abstract class ZImplementationNode extends ZNode {

	private final String functionId;

	@CompilationFinal
	private CallTarget callTarget;

	public ZImplementationNode(String functionId) {
		this.functionId = functionId;
	}

	public CallTarget getCallTarget() {
		if (callTarget == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			callTarget = makeCallTarget();
		}
		return callTarget;
	}

	/**
	 * Lazily create the call target for this implementation node. Only called once,
	 * within the interpreter.
	 */
	protected abstract CallTarget makeCallTarget();

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return new ZImplementation(getCallTarget(), functionId);
	}

}
