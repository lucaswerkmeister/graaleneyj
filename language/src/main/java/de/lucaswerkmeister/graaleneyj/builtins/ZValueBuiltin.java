package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.nodes.ZResolveValueNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZResolveValueNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZValueNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZValueNodeGen;

@NodeInfo(shortName = "value")
public abstract class ZValueBuiltin extends ZBuiltinNode {

	@Child
	private ZResolveValueNode resolveValue = ZResolveValueNodeGen.create();

	@Child
	private ZValueNode value = ZValueNodeGen.create();

	@Specialization
	public Object doGeneric(Object object) {
		return value.execute(resolveValue.execute(object));
	}

}
