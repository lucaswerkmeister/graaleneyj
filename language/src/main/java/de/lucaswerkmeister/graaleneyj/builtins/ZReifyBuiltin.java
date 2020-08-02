package de.lucaswerkmeister.graaleneyj.builtins;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

/**
 * <p>
 * The Z37/reify builtin, turning an object into a list of key / reified value
 * pairs. The order of the returned list is unspecified.
 * </p>
 * TODO:
 * <ul>
 * <li>Handle other specially handled types – Z50/boolean, Z23/nothing, etc.
 * <li>Recursively reify values (turning references into strings, I think)
 * </ul>
 */
@NodeInfo(shortName = "reify")
public abstract class ZReifyBuiltin extends ZBuiltinNode {

	protected static final int LIMIT = 3;

	/** Library for working with a members object. */
	private final InteropLibrary membersLib = InteropLibrary.getFactory().createDispatched(LIMIT);

	@Specialization
	public ZList doString(String value) {
		return new ZList(makePair(ZConstants.ZOBJECT_TYPE, ZConstants.STRING),
				new ZList(makePair(ZConstants.STRING_STRING_VALUE, value), ZList.NIL));
	}

	@Specialization(limit = "LIMIT", guards = { "values.hasMembers(value)" })
	public ZList doGeneric(Object value, @CachedLibrary("value") InteropLibrary values) {
		try {
			ZList ret = ZList.NIL;
			Object members = values.getMembers(value);
			long length = membersLib.getArraySize(members);
			for (long i = length - 1; i >= 0; i--) {
				final String key = (String) membersLib.readArrayElement(members, i);
				final Object member = values.readMember(value, key);
				ret = new ZList(makePair(key, member), ret);
			}
			return ret;
		} catch (UnsupportedMessageException | InvalidArrayIndexException | UnknownIdentifierException e) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			throw new IllegalStateException(e);
		}
	}

	private Object makePair(String key, Object value) {
		return new ZObject(Map.of( //
				ZConstants.ZOBJECT_TYPE, ZConstants.PAIR, //
				ZConstants.PAIR_FIRST, key, //
				ZConstants.PAIR_SECOND, value //
		));
	}

}
