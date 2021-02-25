package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;

public abstract class ZCharacterLiteralNode extends ZNode {

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

	@Specialization
	public Object doGeneral(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "3") DynamicObjectLibrary members) {
		if (extraMembers.length > 0) {
			ZCharacter ret = new ZCharacter(getCharacter(), context.getInitialZObjectShape());
			for (ZCharacterLiteralMemberNode extraMember : extraMembers) {
				members.put(ret, extraMember.key, extraMember.value.execute(virtualFrame));
			}
			return ret;
		} else {
			return ZCharacter.cast(getCharacter());
		}
	}

}
