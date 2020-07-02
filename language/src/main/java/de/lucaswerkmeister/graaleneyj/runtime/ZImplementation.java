package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

@ExportLibrary(InteropLibrary.class)
public class ZImplementation implements TruffleObject {

	private final CallTarget callTarget;
	private final String functionId;

	public ZImplementation(CallTarget callTarget, String functionId) {
		this.callTarget = callTarget;
		this.functionId = functionId;
	}

	public CallTarget getCallTarget() {
		return callTarget;
	}

	@ExportMessage
	public final boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	public final Class<? extends TruffleLanguage<?>> getLanguage() {
		return ZLanguage.class;
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return functionId + " implementation"; // TODO include implementation type?
	}

}
