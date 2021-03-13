package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public class ZFunctionCallNode extends ZNode {

	@Child
	private ZNode function;

	@Children
	private final ZNode[] arguments;

	@Child
	private InteropLibrary library;

	@Child
	private ZResolveValueNode resolveValue = ZResolveValueNodeGen.create();

	public ZFunctionCallNode(ZNode function, ZNode[] arguments) {
		this.function = function;
		this.arguments = arguments;
		this.library = InteropLibrary.getFactory().createDispatched(3);
	}

	@ExplodeLoop
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		Object function = this.function.execute(virtualFrame);
		function = resolveValue.execute(function);

		CompilerAsserts.partialEvaluationConstant(this.arguments.length);
		Object[] arguments = new Object[this.arguments.length];
		for (int i = 0; i < this.arguments.length; i++) {
			arguments[i] = this.arguments[i].execute(virtualFrame);
		}

		try {
			return library.execute(function, arguments);
		} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
			// TODO
			throw new RuntimeException(e);
		}
	}

}
