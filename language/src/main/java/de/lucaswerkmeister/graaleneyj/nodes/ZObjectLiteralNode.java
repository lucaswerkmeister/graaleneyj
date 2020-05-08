package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

public class ZObjectLiteralNode extends ZNode {
	
	public static class ZObjectLiteralMemberNode extends Node {
		@Child private ZNode key;
		@Child private ZNode value;
		
		public ZObjectLiteralMemberNode(ZNode key, ZNode value) {
			this.key = key;
			this.value = value;
		}
	}
	
	@Children private ZObjectLiteralMemberNode[] members;
	
	public ZObjectLiteralNode(ZObjectLiteralMemberNode[] members) {
		this.members = members;
	}
	
	@Override
	public ZObject executeZObject(VirtualFrame virtualFrame) {
		// TODO look at type’s evaluator, …
		Map<String, Object> entries = new HashMap<>();
		for (ZObjectLiteralMemberNode member : members) {
			entries.put(evaluateKey(member, virtualFrame), member.value.execute(virtualFrame));
		}
		return new ZObject(entries);
	}
	
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return executeZObject(virtualFrame);
	}
	
	private String evaluateKey(ZObjectLiteralMemberNode member, VirtualFrame virtualFrame) {
		try {
			return member.key.executeString(virtualFrame);
		} catch (UnexpectedResultException e) {
			throw new UnsupportedSpecializationException(this, new Node[] { member }, e.getResult());
		}
	}

}
