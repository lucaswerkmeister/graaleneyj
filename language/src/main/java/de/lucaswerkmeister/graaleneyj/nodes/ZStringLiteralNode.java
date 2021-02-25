package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZString;

public abstract class ZStringLiteralNode extends ZNode {

	public static class ZStringLiteralMemberNode extends Node {
		private String key;
		@Child
		private ZNode value;

		public ZStringLiteralMemberNode(String key, ZNode value) {
			assert !ZConstants.ZOBJECT_TYPE.equals(key);
			assert !ZConstants.STRING_STRING_VALUE.equals(key);
			this.key = key;
			this.value = value;
		}
	}

	private final String value;

	@Children
	private final ZStringLiteralMemberNode[] extraMembers;

	public ZStringLiteralNode(String value) {
		this(value, new ZStringLiteralMemberNode[0]);
	}

	public ZStringLiteralNode(String value, ZStringLiteralMemberNode[] extraMembers) {
		this.value = value;
		this.extraMembers = extraMembers;
	}

	@Specialization
	public Object doGeneral(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "3") DynamicObjectLibrary members) {
		if (extraMembers.length > 0) {
			ZString ret = new ZString(value, context.getInitialZObjectShape());
			for (ZStringLiteralMemberNode extraMember : extraMembers) {
				members.put(ret, extraMember.key, extraMember.value.execute(virtualFrame));
			}
			return ret;
		} else {
			return value;
		}
	}

}
