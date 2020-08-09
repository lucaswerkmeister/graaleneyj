package de.lucaswerkmeister.graaleneyj.builtins;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNodeGen;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

@NodeInfo(shortName = "value")
public abstract class ZValueBuiltin extends ZBuiltinNode {

	@Child
	private ZEvaluateReferenceNode evaluateReference = ZEvaluateReferenceNodeGen.create();

	// TODO the following specialization won’t work if the input is a reference to a
	// character; split all the specializations of this into a new value node,
	// and then this node only adopts the evaluate reference and value nodes.
	@Specialization
	public Object doCharacter(ZCharacter character) {
		return ZCharacter.cast(character.getCodepoint());
	}

	@Specialization
	public Object getValue(Object object) {
		object = evaluateReference.execute(object);

		if (object instanceof ZObject) {
			try {
				ZObject zobject = (ZObject) object;
				String type = ((ZReference) zobject.readMember(ZConstants.ZOBJECT_TYPE)).getId();
				switch (type) {
				case ZConstants.STRING:
					return zobject.readMember(ZConstants.STRING_STRING_VALUE);
				case ZConstants.BOOLEAN:
					return zobject.readMember(ZConstants.BOOLEAN_IDENTITY);
				case ZConstants.CHARACTER:
					// code adapted from ZCharacterLiteralNode
					// TODO this is probably dead code? there should be no way to create a ZObject
					// with type character, that should be a ZCharacter in the first place
					String character = (String) zobject.readMember(ZConstants.CHARACTER_CHARACTER);
					assert character.codePointCount(0, character.length()) == 1;
					return ZCharacter.cast(character.codePointAt(0));
				}

				Set<String> memberNames = zobject.getMemberNames();
				Map<String, Object> members = new HashMap<>(memberNames.size());
				members.put(ZConstants.ZOBJECT_TYPE, type);
				for (String memberName : memberNames) {
					if (!memberName.startsWith("Z1K")) {
						members.put(memberName, zobject.readMember(memberName));
					}
				}
				return new ZObject(members);
			} catch (UnknownIdentifierException e) {
				// This should never happen; we only read keys that are guaranteed to be present
				throw new RuntimeException(e);
			}
		}

		return object;
	}

}
