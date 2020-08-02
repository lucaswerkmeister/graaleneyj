package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;

public class ZCharacterLiteralNode extends ZNode {

	public static class ZCharacterLiteralMemberNode extends Node {
		private String key;
		@Child
		private ZNode value;

		public ZCharacterLiteralMemberNode(String key, ZNode value) {
			assert !ZConstants.ZOBJECT_TYPE.equals(key);
			assert !ZConstants.CHARACTER_CHARACTER.equals(key);
			this.key = key;
			this.value = value;
		}
	}

	private final String character;

	@Children
	private final ZCharacterLiteralMemberNode[] extraMembers;

	public ZCharacterLiteralNode(String character, ZCharacterLiteralMemberNode[] extraMembers) {
		this.character = character;
		this.extraMembers = extraMembers;
	}

	private int getCharacter() {
		// TODO assert or always check?
		assert character.codePointCount(0, character.length()) == 1;
		return character.codePointAt(0);
	}

	@Override
	public int executeCharacter(VirtualFrame virtualFrame) throws UnexpectedResultException {
		if (extraMembers.length > 0) {
			throw new UnexpectedResultException(execute(virtualFrame));
		} else {
			return getCharacter();
		}
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		if (extraMembers.length > 0) {
			Map<String, Object> extraEntries = new HashMap<>();
			for (ZCharacterLiteralMemberNode extraMember : extraMembers) {
				extraEntries.put(extraMember.key, extraMember.value.execute(virtualFrame));
			}
			return new ZCharacter(getCharacter(), extraEntries);
		} else {
			return ZCharacter.cast(getCharacter());
		}
	}

}
