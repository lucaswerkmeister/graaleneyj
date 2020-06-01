package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

public class ZReferenceLiteralNode extends ZNode {

	private final String id;

	public ZReferenceLiteralNode(String id) {
		this.id = id;
	}

	@Override
	public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
		switch (id) {
		case ZConstants.TRUE:
			return true;
		case ZConstants.FALSE:
			return false;
		default:
			throw new UnexpectedResultException(execute(virtualFrame));
		}
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		switch (id) {
		case ZConstants.TRUE:
			return true;
		case ZConstants.FALSE:
			return false;
		case ZConstants.NIL:
			return ZList.NIL;
		default:
			return new ZReference(id);
		}
	}

}
