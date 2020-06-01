package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ZReadArgumentNode extends ZNode {

	private final int index;

	public ZReadArgumentNode(int index) {
		assert index >= 0;
		this.index = index;
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return virtualFrame.getArguments()[index]; // TODO what if index >= args.length?
	}

}
