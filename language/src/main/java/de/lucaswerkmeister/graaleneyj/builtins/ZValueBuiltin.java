package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZEvaluateReferenceNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZValueNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZValueNodeGen;

@NodeInfo(shortName = "value")
public abstract class ZValueBuiltin extends ZBuiltinNode {

	@Child
	private ZEvaluateReferenceNode evaluateReference = ZEvaluateReferenceNodeGen.create();

	@Child
	private ZValueNode value = ZValueNodeGen.create();

	@Specialization
	public Object doGeneric(Object object) {
		return value.execute(evaluateReference.execute(object));
	}

}
