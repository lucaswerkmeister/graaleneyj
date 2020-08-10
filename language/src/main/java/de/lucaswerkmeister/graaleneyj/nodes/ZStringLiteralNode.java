package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.runtime.ZString;

public class ZStringLiteralNode extends ZNode {

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

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		if (extraMembers.length > 0) {
			Map<String, Object> extraEntries = new HashMap<>();
			for (ZStringLiteralMemberNode extraMember : extraMembers) {
				extraEntries.put(extraMember.key, extraMember.value.execute(virtualFrame));
			}
			return new ZString(value, extraEntries);
		} else {
			return value;
		}
	}

}
