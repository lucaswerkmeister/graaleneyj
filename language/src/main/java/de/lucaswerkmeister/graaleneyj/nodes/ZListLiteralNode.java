package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;

public class ZListLiteralNode extends ZNode {
	
	@Children private final ZNode[] nodes;
	
	public ZListLiteralNode(ZNode[] nodes) {
		this.nodes = nodes;
	}

	@Override
	@ExplodeLoop
	public ZList executeZList(VirtualFrame virtualFrame) {
		CompilerAsserts.compilationConstant(nodes.length);
		Object[] values = new Object[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			values[i] = nodes[i].execute(virtualFrame);
		}

		ZList list = ZList.NIL;
		for (int i = nodes.length - 1; i >= 0; i--) {
			list = new ZList(values[i], list);
		}
		return list;
	}
	
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return executeZList(virtualFrame);
	}

}
