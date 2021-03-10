package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleContext;
import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.source.Source;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public final class ZContext {

	private final Env env;

	private final Shape initialZObjectShape;

	private final String userLanguage;

	private final boolean useInnerContexts;

	private final DynamicObject persistentObjectRegistry;

	private final Lock persistentObjectLibraryLock = new ReentrantLock();

	public ZContext(Env env, Shape initialZObjectShape) {
		this.env = env;
		this.initialZObjectShape = initialZObjectShape;
		this.userLanguage = env.getOptions().get(ZLanguage.userLanguage);
		this.useInnerContexts = env.getOptions().get(ZLanguage.useInnerContexts);
		this.persistentObjectRegistry = new ZPersistentObjectRegistry(initialZObjectShape); // TODO same shape?
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

	/**
	 * <p>
	 * The registry of persistent objects for this context.
	 * </p>
	 * <p>
	 * The registry is a dynamic object storing the persistent objects, using their
	 * ID as the key. Any key may change state exactly once, from unassigned to
	 * assigned; afterwards, it may never change again, nor may it be deleted.
	 * </p>
	 * <p>
	 * Writes to the registry must be guarded by
	 * {@link #getPersistentObjectRegistryLock its lock}.
	 * </p>
	 */
	public DynamicObject getPersistentObjectRegistry() {
		return persistentObjectRegistry;
	}

	/**
	 * Lock for the {@link #getPersistentObjectRegistry persistent object registry}.
	 */
	public Lock getPersistentObjectRegistryLock() {
		return persistentObjectLibraryLock;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public boolean useInnerContexts() {
		return useInnerContexts;
	}

}
