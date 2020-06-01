package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.TruffleObject;

public class ZImplementation implements TruffleObject {

	private final RootCallTarget callTarget;

	public ZImplementation(RootCallTarget callTarget) {
		this.callTarget = callTarget;
	}

	public RootCallTarget getCallTarget() {
		return callTarget;
	}

}
