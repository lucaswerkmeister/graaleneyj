package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public final class ZContext {

	private final Env env;

	private final Shape initialZObjectShape;

	private final Map<String, Object> objects = new HashMap<>();

	public ZContext(Env env, Shape initialZObjectShape) {
		this.env = env;
		this.initialZObjectShape = initialZObjectShape;
	}

	public TruffleFile getTruffleFile(String zid) {
		return env.getInternalTruffleFile(WikiLambdaFileSystem.zidToPath(zid).toString());
	}

	public boolean canParseLanguage(String language) {
		return env.getPublicLanguages().containsKey(language);
	}

	public CallTarget parse(Source source, String... argumentNames) {
		return env.parsePublic(source, argumentNames);
	}

	public ZObject makeObject(Map<String, Object> members) {
		return new ZObject(initialZObjectShape, members);
	}

	public boolean hasObject(String zid) {
		return objects.containsKey(zid);
	}

	public Object getObject(String zid) {
		assert hasObject(zid);
		return objects.get(zid);
	}

	public void putObject(String zid, Object object) {
		assert !hasObject(zid);
		assert object != null;
		objects.put(zid, object);
	}

	public TruffleObject loadError(String zid) {
		Object error;
		if (hasObject(zid)) {
			error = getObject(zid);
		} else {
			ZReference errorReference = new ZReference(zid, this);
			try {
				error = InteropLibrary.getFactory().getUncached().execute(errorReference);
			} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
				throw new RuntimeException(e);
			}
		}
		assert error instanceof TruffleObject;
		return (TruffleObject) error;
	}

}
