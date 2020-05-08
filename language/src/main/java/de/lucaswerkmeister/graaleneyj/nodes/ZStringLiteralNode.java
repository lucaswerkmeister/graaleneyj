package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ZStringLiteralNode extends ZNode {
	
	private final String value;
	
	public ZStringLiteralNode(String value) {
		this.value = value;
	}
	
	@Override
	public String execute(VirtualFrame frame) {
		return value;
	}

}
