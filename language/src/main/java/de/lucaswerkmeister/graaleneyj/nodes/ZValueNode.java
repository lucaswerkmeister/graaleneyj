package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltin;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;
import de.lucaswerkmeister.graaleneyj.runtime.ZString;

/**
 * Helper node to implement the {@link ZValueBuiltin Z36/value builtin}, after
 * references have been evaluated.
 */
// TODO this class should probably no longer be needed at all?
public abstract class ZValueNode extends Node {

	protected static final int LIMIT = 3;

	/** Library for working with a members object. */
	private final InteropLibrary membersLib = InteropLibrary.getFactory().createDispatched(LIMIT);

	/** Library for adding members to the newly created values object. */
	@Child
	protected DynamicObjectLibrary objectLib = DynamicObjectLibrary.getFactory().createDispatched(LIMIT);

	/** Library for adding the ZOBJECT_TYPE member to the newly created object. */
	@Child
	protected DynamicObjectLibrary typeLib = DynamicObjectLibrary.getFactory().createDispatched(LIMIT);

	public abstract Object execute(Object value);

	@Specialization
	public Object doCharacter(ZCharacter character) {
		return ZCharacter.cast(character.getCodepoint());
	}

	@Specialization
	public Object doString(ZString string) {
		return string.asString();
	}

	@Specialization(limit = "LIMIT", guards = { "values.hasMembers(value)" })
	public Object doGeneric(Object value, @CachedLibrary("value") InteropLibrary values,
			@CachedContext(ZLanguage.class) ZContext context) {
		// TODO use multiple InteropLibrary instances for different keys?
		try {
			String type = ((ZReference) values.readMember(value, ZConstants.ZOBJECT_TYPE)).getId();
			switch (type) {
			case ZConstants.STRING:
				// TODO this should be dead code, there should be no way to get a ZObject of
				// type string in the first place
				return values.readMember(value, ZConstants.STRING_STRING_VALUE);
			case ZConstants.BOOLEAN:
				return values.readMember(value, ZConstants.BOOLEAN_IDENTITY);
			case ZConstants.CHARACTER:
				// code adapted from ZCharacterLiteralNode
				// TODO this is probably dead code? there should be no way to create a ZObject
				// with type character, that should be a ZCharacter in the first place
				String character = (String) values.readMember(value, ZConstants.CHARACTER_CHARACTER);
				assert character.codePointCount(0, character.length()) == 1;
				return ZCharacter.cast(character.codePointAt(0));
			}

			DynamicObject object = context.makeObject();
			Object members = values.getMembers(value);
			long length = membersLib.getArraySize(members);
			typeLib.put(object, ZConstants.ZOBJECT_TYPE, type);
			for (long i = 0; i < length; i++) {
				final String key = (String) membersLib.readArrayElement(members, i);
				if (!key.startsWith("Z1K")) {
					objectLib.put(object, key, values.readMember(value, key));
				}
			}
			return object;
		} catch (UnknownIdentifierException | UnsupportedMessageException | InvalidArrayIndexException e) {
			// This should never happen; we only read keys that are guaranteed to be present
			throw new RuntimeException(e);
		}
	}

	@Fallback
	public Object doOther(Object object) {
		return object;
	}

}
