package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltin;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

/**
 * Helper node to implement the {@link ZValueBuiltin Z36/value builtin}, after
 * references have been evaluated.
 */
public abstract class ZValueNode extends Node {

	public abstract Object execute(Object value);

	@Specialization
	public Object doCharacter(ZCharacter character) {
		return ZCharacter.cast(character.getCodepoint());
	}

	@Specialization
	public Object doZObject(ZObject object) {
		try {
			String type = ((ZReference) object.readMember(ZConstants.ZOBJECT_TYPE)).getId();
			switch (type) {
			case ZConstants.STRING:
				return object.readMember(ZConstants.STRING_STRING_VALUE);
			case ZConstants.BOOLEAN:
				return object.readMember(ZConstants.BOOLEAN_IDENTITY);
			case ZConstants.CHARACTER:
				// code adapted from ZCharacterLiteralNode
				// TODO this is probably dead code? there should be no way to create a ZObject
				// with type character, that should be a ZCharacter in the first place
				String character = (String) object.readMember(ZConstants.CHARACTER_CHARACTER);
				assert character.codePointCount(0, character.length()) == 1;
				return ZCharacter.cast(character.codePointAt(0));
			}

			Set<String> memberNames = object.getMemberNames();
			Map<String, Object> members = new HashMap<>(memberNames.size());
			members.put(ZConstants.ZOBJECT_TYPE, type);
			for (String memberName : memberNames) {
				if (!memberName.startsWith("Z1K")) {
					members.put(memberName, object.readMember(memberName));
				}
			}
			return new ZObject(members);
		} catch (UnknownIdentifierException e) {
			// This should never happen; we only read keys that are guaranteed to be present
			throw new RuntimeException(e);
		}
	}

	@Fallback
	public Object doOther(Object object) {
		return object;
	}

}
