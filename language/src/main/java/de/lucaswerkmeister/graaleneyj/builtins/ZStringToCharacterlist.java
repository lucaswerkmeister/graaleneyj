package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.Shape;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;

@NodeInfo(shortName = "string_to_characterlist")
public abstract class ZStringToCharacterlist extends ZBuiltinNode {

	@Specialization
	@TruffleBoundary
	public ZList doString(String s, @CachedContext(ZLanguage.class) ZContext context) {
		Shape shape = context.getInitialZObjectShape();
		ZList ret = ZList.NIL;
		for (int i = s.length(); i > 0;) {
			int character = s.codePointBefore(i);
			ret = new ZList(ZCharacter.cast(character, shape), ret);
			i -= Character.isBmpCodePoint(character) ? 1 : 2;
		}
		return ret;
	}

}
