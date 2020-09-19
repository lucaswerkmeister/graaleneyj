package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.builtins.ZReifyBuiltin;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to recursively reify a value. Unlike {@link ZReifyBuiltin},
 * references and strings are mapped to plain strings.
 */
public abstract class ZReifyNode extends Node {

	protected static final int LIMIT = 3;

	/** Library for working with a members object. */
	private final InteropLibrary membersLib = InteropLibrary.getFactory().createDispatched(LIMIT);

	public abstract Object execute(Object value);

	@Specialization
	public Object doString(String value) {
		return value;
	}

	@Specialization
	public Object doReference(ZReference value) {
		return value;
	}

	// note: that guard also ensures that this specialization does not match String
	// and ZReference
	@Specialization(limit = "LIMIT", guards = { "values.hasMembers(value)" })
	public Object doGeneric(Object value, @CachedLibrary("value") InteropLibrary values) {
		try {
			ZList ret = ZList.NIL;
			Object members = values.getMembers(value);
			long length = membersLib.getArraySize(members);
			for (long i = length - 1; i >= 0; i--) {
				final String key = (String) membersLib.readArrayElement(members, i);
				final Object member = values.readMember(value, key);
				ret = new ZList(makePair(key, execute(member)), ret);
			}
			return ret;
		} catch (UnsupportedMessageException | InvalidArrayIndexException | UnknownIdentifierException e) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			throw new IllegalStateException(e);
		}
	}

	public Object makePair(String key, Object value) {
		// TODO @CachedContext?
		return lookupContextReference(ZLanguage.class).get().makeObject(Map.<String, Object>of( //
				ZConstants.ZOBJECT_TYPE, new ZReference(ZConstants.PAIR, null), // TODO context
				ZConstants.PAIR_FIRST, key, //
				ZConstants.PAIR_SECOND, value //
		));
	}

}
