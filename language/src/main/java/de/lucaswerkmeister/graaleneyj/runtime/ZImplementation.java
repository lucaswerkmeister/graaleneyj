package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.TruffleObject;

public class ZImplementation implements TruffleObject {

	private final CallTarget callTarget;

	public ZImplementation(CallTarget callTarget) {
		this.callTarget = callTarget;
	}

	public CallTarget getCallTarget() {
		return callTarget;
	}

}
