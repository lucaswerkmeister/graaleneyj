package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.source.Source;

public final class ZContext { // TODO find out what things we need from the context

	private final Env env;

	public ZContext(Env env) {
		this.env = env;
	}

	public TruffleFile getTruffleFile(String zid) {
		return env.getInternalTruffleFile("abstracttext/eneyj/data/" + zid + ".json");
	}

	public CallTarget parse(Source source, String... argumentNames) {
		return env.parsePublic(source, argumentNames);
	}

}
