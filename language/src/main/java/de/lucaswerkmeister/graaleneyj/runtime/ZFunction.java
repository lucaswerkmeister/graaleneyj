package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.utilities.CyclicAssumption;

@ExportLibrary(InteropLibrary.class)
public class ZFunction implements TruffleObject {

	private final ZImplementation[] implementations;
	private int implementationIndex = 0;
	private final CyclicAssumption implementationIndexStable;

	public ZFunction(ZImplementation[] implementations) {
		this.implementations = implementations;
		implementationIndexStable = new CyclicAssumption("TODO function name goes here");
	}

	public void setImplementationIndex(int index) {
		assert index >= 0 && index < implementations.length;
		this.implementationIndex = index;
		implementationIndexStable.invalidate();
	}

	public CallTarget getCallTarget() {
		return implementations[implementationIndex].getCallTarget();
	}

	public Assumption getCallTargetStable() {
		return implementationIndexStable.getAssumption();
	}

	@ExportMessage
	public boolean isExecutable() {
		return true;
	}

	@ExportMessage
	public abstract static class Execute {
		@Specialization(guards = "function.getCallTarget() == cachedTarget", assumptions = "callTargetStable")
		protected static Object doDirect(ZFunction function, Object[] arguments,
				@Cached("function.getCallTargetStable()") Assumption callTargetStable,
				@Cached("function.getCallTarget()") CallTarget cachedTarget,
				@Cached("create(cachedTarget)") DirectCallNode callNode) {
			return callNode.call(arguments);
		}

		@Specialization(replaces = "doDirect")
		protected static Object doIndirect(ZFunction function, Object[] arguments, @Cached IndirectCallNode callNode) {
			return callNode.call(function.getCallTarget(), arguments);
		}
	}

}
