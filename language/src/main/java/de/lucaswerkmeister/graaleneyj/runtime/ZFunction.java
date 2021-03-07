package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.ArrayList;
import java.util.Collection;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.utilities.CyclicAssumption;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.library.ZTypeIdentityLibrary;

@ExportLibrary(ZTypeIdentityLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class ZFunction extends ZObject {

	private final ZImplementation[] implementations;
	private final String id;
	private int implementationIndex = 0;
	private Collection<Integer> unusableImplementations = null;
	private final CyclicAssumption implementationIndexStable;

	public ZFunction(ZImplementation[] implementations, String id) {
		super(STATIC_BLANK_SHAPE);
		assert id != null;
		this.implementations = implementations;
		this.id = id;
		implementationIndexStable = new CyclicAssumption("ZFunction " + id);
	}

	public void setImplementationIndex(int index) {
		assert index >= 0 && index < implementations.length;
		this.implementationIndex = index;
		implementationIndexStable.invalidate();
	}

	/**
	 * Mark the current execution as unusable, and choose a different one.
	 */
	private void markCurrentImplementationUnusable() {
		if (unusableImplementations == null) {
			unusableImplementations = new ArrayList<>(2);
		}
		unusableImplementations.add(implementationIndex);
		for (int i = 0; i < implementations.length; i++) {
			if (!unusableImplementations.contains(i)) {
				setImplementationIndex(i);
				return;
			}
		}
		throw new UnusableImplementationException("No usable implementation left: " + id);
		// TODO mark unusable functions more efficiently, this way the same index will
		// be added to unusableImplementations again and again
	}

	/**
	 * Handle an {@link UnusableImplementationException} by choosing a different
	 * implementation and then re-executing the function.
	 */
	@TruffleBoundary
	private Object handleUnusableImplementationException(UnusableImplementationException unusable, Object[] arguments) {
		markCurrentImplementationUnusable();
		try {
			return InteropLibrary.getFactory().getUncached().execute(this, arguments);
		} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
			// TODO
			throw new RuntimeException(e);
		}
	}

	public CallTarget getCallTarget() {
		return implementations[implementationIndex].getCallTarget();
	}

	public Assumption getCallTargetStable() {
		return implementationIndexStable.getAssumption();
	}

	@ExportMessage
	public String getTypeIdentity() {
		return ZConstants.FUNCTION;
	}

	@ExportMessage
	public boolean isExecutable() {
		return true;
	}

	@ExportMessage
	public abstract static class Execute {
		@Specialization(assumptions = "callTargetStable")
		protected static Object doDirect(ZFunction function, Object[] arguments,
				@Cached("function.getCallTargetStable()") Assumption callTargetStable,
				@Cached("function.getCallTarget()") CallTarget cachedTarget,
				@Cached("create(cachedTarget)") DirectCallNode callNode) {
			try {
				return callNode.call(arguments);
			} catch (UnusableImplementationException e) {
				return function.handleUnusableImplementationException(e, arguments);
			}
		}

		@Specialization(replaces = "doDirect")
		protected static Object doIndirect(ZFunction function, Object[] arguments, @Cached IndirectCallNode callNode) {
			try {
				return callNode.call(function.getCallTarget(), arguments);
			} catch (UnusableImplementationException e) {
				return function.handleUnusableImplementationException(e, arguments);
			}
		}
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return id;
	}

	public boolean equals(Object obj) {
		return obj instanceof ZFunction && id.equals(((ZFunction) obj).id);
	}

}
