package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;

public class ZCharacterLiteralNode extends ZNode {

	private final String character;

	public ZCharacterLiteralNode(String character) {
		this.character = character;
	}

	@Override
	public int executeCharacter(VirtualFrame virtualFrame) {
		// TODO assert or always check?
		assert character.codePointCount(0, character.length()) == 1;
		return character.codePointAt(0);
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return ZCharacter.cast(executeCharacter(virtualFrame));
	}

}
