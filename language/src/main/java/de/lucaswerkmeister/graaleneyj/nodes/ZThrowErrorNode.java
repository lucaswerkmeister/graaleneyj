package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZErrorException;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Auxiliary node to throw a {@link ZErrorException} wrapping the object with a
 * certain ID.
 */
public abstract class ZThrowErrorNode extends Node {

	public abstract void execute(String id);

	@Specialization(guards = "context.hasObject(id)")
	public void doExisting(String id, @CachedContext(ZLanguage.class) ZContext context) {
		throw new ZErrorException((TruffleObject) context.getObject(id), this);
	}

	@Specialization
	public void doNew(String id, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "1") InteropLibrary interop) {
		try {
			ZReference reference = new ZReference(id);
			Object error = interop.execute(reference);
			throw new ZErrorException((TruffleObject) error, this);
		} catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
			throw new IllegalStateException(e);
		}
	}

}
