package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
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
	public Object doList(ZList list, @CachedLibrary(limit = "3") InteropLibrary pairs) {
		// TODO use multiple InteropLibrary instances for different keys?
		try {
			Map<String, Object> members = new HashMap<>();
			while (list != ZList.NIL) {
				Object pair = list.getHead();
				assert (ZConstants.PAIR.equals(((ZReference) pairs.readMember(pair, ZConstants.ZOBJECT_TYPE)).getId()));
				String key = (String) pairs.readMember(pair, ZConstants.PAIR_FIRST); // TODO proper error handling
				Object value = pairs.readMember(pair, ZConstants.PAIR_SECOND);
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
			case ZConstants.LIST:
				members.remove(ZConstants.ZOBJECT_TYPE);
				Object head = members.remove(ZConstants.LIST_HEAD);
				Object tail = members.remove(ZConstants.LIST_TAIL);
				Object id = members.remove(ZConstants.ZOBJECT_ID);
				if (!members.isEmpty()) {
					throw new IllegalArgumentException("List must not have extra members"); // TODO
				}
				if (id == null) {
					return new ZList(head, (ZList) tail); // TODO proper error handling
				} else {
					if (!(id instanceof ZReference)) {
						throw new IllegalArgumentException("ID of list must be reference"); // TODO
					}
					if (!ZConstants.NIL.equals(((ZReference) id).getId())) {
						throw new IllegalArgumentException("No list other than nil may have an ID"); // TODO
					}
					if (!(head instanceof ZReference)) {
						throw new IllegalArgumentException("Head of nil must be reference"); // TODO
					}
					if (!ZConstants.LISTISNIL.equals(((ZReference) head).getId())) {
						throw new IllegalArgumentException("Head of nil must be list_is_nil"); // TODO
					}
					if (!(tail instanceof ZReference)) {
						throw new IllegalArgumentException("Tail of nil must be reference"); // TODO
					}
					if (!ZConstants.LISTISNIL.equals(((ZReference) tail).getId())) {
						throw new IllegalArgumentException("Tail of nil must be list_is_nil"); // TODO
					}
					return ZList.NIL;
				}
			case ZConstants.CHARACTER:
				members.remove(ZConstants.ZOBJECT_TYPE);
				Object character = members.remove(ZConstants.CHARACTER_CHARACTER);
				return new ZCharacter(((String) character).codePointAt(0), members); // TODO proper error handling
			}
			return new ZObject(members);
		} catch (UnknownIdentifierException | UnsupportedMessageException e) {
			throw new RuntimeException(e); // TODO
		}
	}

}
