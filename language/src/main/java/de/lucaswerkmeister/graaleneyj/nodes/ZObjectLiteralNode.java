package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZPlainObject;

public abstract class ZObjectLiteralNode extends ZNode {

	public static class ZObjectLiteralMemberNode extends Node {
		private String key;
		@Child
		private ZNode value;

		public ZObjectLiteralMemberNode(String key, ZNode value) {
			this.key = key;
			this.value = value;
		}
	}

	@Children
	private ZObjectLiteralMemberNode[] members;

	public ZObjectLiteralNode(ZObjectLiteralMemberNode[] members) {
		this.members = members;
	}

	@Specialization
	public Object doGeneric(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "3") DynamicObjectLibrary putMember) {
		DynamicObject object = new ZPlainObject(context.getInitialZObjectShape());
		for (ZObjectLiteralMemberNode member : members) {
			putMember.put(object, member.key, member.value.execute(virtualFrame));
		}
		return object;
	}

}
