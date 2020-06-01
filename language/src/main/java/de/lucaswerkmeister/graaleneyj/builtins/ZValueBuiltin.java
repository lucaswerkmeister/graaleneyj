package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

@NodeInfo(shortName = "value")
public abstract class ZValueBuiltin extends ZBuiltinNode {

	@Specialization
	public Object getValue(Object object) {
		if (object instanceof ZReference) {
			ZReference reference = (ZReference) object;
			if ("Z28".equals(reference.getId())) {
				return "eneyj"; // TODO hard-coded
			}
		}

		return object; // TODO rest of the fucking owl
	}

}
