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
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

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

	@Child
	private ZNode type;

	@Children
	private ZObjectLiteralMemberNode[] otherMembers;

	@Child
	protected DynamicObjectLibrary objectLib = DynamicObjectLibrary.getFactory().createDispatched(3);

	public ZObjectLiteralNode(ZNode type, ZObjectLiteralMemberNode[] otherMembers) {
		this.type = type;
		this.otherMembers = otherMembers;
	}

	@Specialization
	public Object doGeneric(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context,
			@CachedLibrary(limit = "3") DynamicObjectLibrary putType) {
		ZObject object = context.makeObject();
		putType.put(object, ZConstants.ZOBJECT_TYPE, type.execute(virtualFrame));
		for (ZObjectLiteralMemberNode member : otherMembers) {
			objectLib.put(object, member.key, member.value.execute(virtualFrame));
		}
		return object;
	}

}
