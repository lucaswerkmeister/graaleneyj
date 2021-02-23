package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZNothing;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

public abstract class ZReferenceLiteralNode extends ZNode {

	private final String id;

	public ZReferenceLiteralNode(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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

	@Specialization
	public Object executeGeneric(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context) {
		switch (id) {
		case ZConstants.TRUE:
			return true;
		case ZConstants.FALSE:
			return false;
		case ZConstants.NIL:
			return ZList.NIL;
		case ZConstants.NOTHING:
			return ZNothing.INSTANCE;
		default:
			return new ZReference(id, context.getInitialZObjectShape());
		}
	}

}
