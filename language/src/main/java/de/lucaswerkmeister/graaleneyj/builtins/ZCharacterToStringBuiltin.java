package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;

@NodeInfo(shortName = "character_to_string")
public abstract class ZCharacterToStringBuiltin extends ZBuiltinNode {

	@Specialization
	public String doCharacter(int character) {
		return Character.toString(character);
	}

	@Specialization
	public String doZCharacter(ZCharacter character) {
		return character.asString();
	}

	// TODO @Specialization throwing an argument_type_mismatch for other arguments

}
