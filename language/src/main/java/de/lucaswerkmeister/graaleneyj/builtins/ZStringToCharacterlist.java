package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;

@NodeInfo(shortName = "string_to_characterlist")
public abstract class ZStringToCharacterlist extends ZBuiltinNode {

	@Specialization
	public ZList doString(String s) {
		ZList ret = ZList.NIL;
		for (int i = s.length(); i > 0;) {
			int character = s.codePointBefore(i);
			ret = new ZList(ZCharacter.cast(character), ret);
			i -= Character.isBmpCodePoint(character) ? 1 : 2;
		}
		return ret;
	}

}
