package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

@TypeSystemReference(ZTypes.class)
@NodeInfo(language = "Z language", description = "The abstract base node.") // TODO Z language? Eneyj language?
public abstract class ZNode extends Node {

	public abstract Object execute(VirtualFrame virtualFrame);

	public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectBoolean(execute(virtualFrame));
	}

	public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectString(execute(virtualFrame));
	}

	public ZList executeZList(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectZList(execute(virtualFrame));
	}

	public ZObject executeZObject(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectZObject(execute(virtualFrame));
	}

}
