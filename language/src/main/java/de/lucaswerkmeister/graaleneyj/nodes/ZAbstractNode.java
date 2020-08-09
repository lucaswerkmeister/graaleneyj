package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.builtins.ZAbstractBuiltin;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;
import de.lucaswerkmeister.graaleneyj.runtime.ZString;

/**
 * Helper node to recursively abstract a reified list of key / reified value
 * pairs. Unlike {@link ZAbstractBuiltin}, this supports non-list inputs.
 */
public abstract class ZAbstractNode extends Node {

	public abstract Object execute(Object value);

	@Specialization
	public Object doString(String string) {
		return string;
	}

	@Specialization
	public Object doReference(ZReference reference) {
		return reference;
	}

	@Specialization
	public Object doList(ZList list) {
		try {
			Map<String, Object> members = new HashMap<>();
			while (list != ZList.NIL) {
				ZObject pair = (ZObject) list.getHead(); // TODO proper error handling
				assert "Z2".equals(((ZReference) pair.readMember(ZConstants.ZOBJECT_TYPE)).getId());
				String key = (String) pair.readMember(ZConstants.PAIR_FIRST); // TODO proper error handling
				Object value = pair.readMember(ZConstants.PAIR_SECOND);
				members.put(key, execute(value));
				list = list.getTail();
			}
			switch (((ZReference) members.get(ZConstants.ZOBJECT_TYPE)).getId()) { // TODO proper error handling
			// TODO more cases for more specially handled types
			case ZConstants.STRING:
				members.remove(ZConstants.ZOBJECT_TYPE);
				Object string = members.remove(ZConstants.STRING_STRING_VALUE);
				if (members.isEmpty()) {
					return string;
				} else {
					return new ZString((String) string, members); // TODO proper error handling
				}
			case ZConstants.CHARACTER:
				members.remove(ZConstants.ZOBJECT_TYPE);
				Object character = members.remove(ZConstants.CHARACTER_CHARACTER);
				return new ZCharacter(((String) character).codePointAt(0), members); // TODO proper error handling
			}
			return new ZObject(members);
		} catch (UnknownIdentifierException e) {
			throw new RuntimeException(e); // TODO
		}
	}

}
