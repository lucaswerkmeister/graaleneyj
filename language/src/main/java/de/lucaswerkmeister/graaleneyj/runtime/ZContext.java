package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;

public final class ZContext {

	private final Env env;

	private final Map<String, Object> objects = new HashMap<>();

	public ZContext(Env env) {
		this.env = env;
	}

	public TruffleFile getTruffleFile(String zid) {
		return env.getInternalTruffleFile("abstracttext/eneyj/data/" + zid + ".json");
	}

	public CallTarget parse(Source source, String... argumentNames) {
		return env.parsePublic(source, argumentNames);
	}

	public boolean hasObject(String zid) {
		return objects.containsKey(zid);
	}

	public Object getObject(String zid) {
		return objects.get(zid);
	}

	public void putObject(String zid, Object object) {
		assert !hasObject(zid);
		objects.put(zid, object);
	}

	public TruffleObject loadError(String zid) {
		Object error;
		if (hasObject(zid)) {
			error = getObject(zid);
		} else {
			ZReference errorReference = new ZReference(zid, this);
			error = errorReference.evaluate();
		}
		assert error instanceof TruffleObject;
		return (TruffleObject) error;
	}

}
