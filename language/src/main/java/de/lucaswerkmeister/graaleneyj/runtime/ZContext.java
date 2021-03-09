package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleContext;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public final class ZContext {

	private final Env env;

	private final Shape initialZObjectShape;

	private final String userLanguage;

	private final boolean useInnerContexts;

	private final Map<String, Object> objects = new HashMap<>();

	public ZContext(Env env, Shape initialZObjectShape) {
		this.env = env;
		this.initialZObjectShape = initialZObjectShape;
		this.userLanguage = env.getOptions().get(ZLanguage.userLanguage);
		this.useInnerContexts = env.getOptions().get(ZLanguage.useInnerContexts);
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

	public TruffleContext makeInnerContext() {
		return env.newContextBuilder().build();
	}

	public Shape getInitialZObjectShape() {
		return initialZObjectShape;
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

	public String getUserLanguage() {
		return userLanguage;
	}

	public boolean useInnerContexts() {
		return useInnerContexts;
	}

}
