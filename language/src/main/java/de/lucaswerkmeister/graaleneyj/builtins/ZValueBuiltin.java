package de.lucaswerkmeister.graaleneyj.builtins;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

@NodeInfo(shortName = "value")
public abstract class ZValueBuiltin extends ZBuiltinNode {

	@Specialization
	public Object getValue(Object object) {
		while (object instanceof ZReference) {
			object = ((ZReference) object).evaluate();
		}

		if (object instanceof ZObject) {
			ZObject zobject = (ZObject) object;
			String type = ((ZReference) zobject.readMember(ZConstants.ZOBJECT_TYPE)).getId();
			switch (type) {
			case ZConstants.STRING:
				return zobject.readMember(ZConstants.STRING_STRING_VALUE);
			case ZConstants.BOOLEAN:
				return zobject.readMember(ZConstants.BOOLEAN_IDENTITY);
			}

			String[] memberNames = zobject.getMembers(false);
			Map<String, Object> members = new HashMap<>(memberNames.length);
			for (String memberName : memberNames) {
				if (!memberName.startsWith("Z1K") || memberName.equals(ZConstants.ZOBJECT_TYPE)) {
					members.put(memberName, zobject.readMember(memberName));
				}
			}
			return new ZObject(members);
		}

		return object;
	}

}
