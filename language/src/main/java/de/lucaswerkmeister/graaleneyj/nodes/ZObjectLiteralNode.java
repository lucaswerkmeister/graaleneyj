package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

public class ZObjectLiteralNode extends ZNode {

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

	@Override
	public ZObject executeZObject(VirtualFrame virtualFrame) {
		// TODO look at type’s evaluator, …
		Map<String, Object> entries = new HashMap<>();
		for (ZObjectLiteralMemberNode member : members) {
			entries.put(member.key, member.value.execute(virtualFrame));
		}
		// TODO @CachedContext?
		ZContext context = lookupContextReference(ZLanguage.class).get();
		return context.makeObject(entries);
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return executeZObject(virtualFrame);
	}

}
