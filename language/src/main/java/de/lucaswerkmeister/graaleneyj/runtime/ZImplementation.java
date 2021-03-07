package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZConstants;

@ExportLibrary(InteropLibrary.class)
public class ZImplementation extends ZObject {

	private final CallTarget callTarget;
	private final String functionId;

	public ZImplementation(CallTarget callTarget, String functionId) {
		super(STATIC_BLANK_SHAPE);
		this.callTarget = callTarget;
		this.functionId = functionId;
	}

	public CallTarget getCallTarget() {
		return callTarget;
	}

	@Override
	String getTypeIdentity(DynamicObjectLibrary objects) {
		return ZConstants.IMPLEMENTATION;
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return functionId + " implementation"; // TODO include implementation type?
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ZImplementation && functionId.equals(((ZImplementation) obj).functionId);
	}

}
