package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public abstract class ZFunctionCallNode extends ZNode {

	@Child
	private ZNode function;

	@Children
	private final ZNode[] arguments;

	public ZFunctionCallNode(ZNode function, ZNode[] arguments) {
		this.function = function;
		this.arguments = arguments;
	}

	@ExplodeLoop
	@Specialization
	public Object doGeneric(VirtualFrame virtualFrame, @CachedLibrary(limit = "3") InteropLibrary functions,
			@Cached("create()") ZResolveValueNode resolveValue) {
		Object function = this.function.execute(virtualFrame);
		function = resolveValue.execute(function);

		CompilerAsserts.partialEvaluationConstant(this.arguments.length);
		Object[] arguments = new Object[this.arguments.length];
		for (int i = 0; i < this.arguments.length; i++) {
			arguments[i] = this.arguments[i].execute(virtualFrame);
		}

		try {
			return functions.execute(function, arguments);
		} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
			// TODO
			throw new RuntimeException(e);
		}
	}

}
