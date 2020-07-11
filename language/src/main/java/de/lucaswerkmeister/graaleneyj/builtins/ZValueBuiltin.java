package de.lucaswerkmeister.graaleneyj.builtins;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
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
			try {
				ZObject zobject = (ZObject) object;
				String type = ((ZReference) zobject.readMember(ZConstants.ZOBJECT_TYPE)).getId();
				switch (type) {
				case ZConstants.STRING:
					return zobject.readMember(ZConstants.STRING_STRING_VALUE);
				case ZConstants.BOOLEAN:
					return zobject.readMember(ZConstants.BOOLEAN_IDENTITY);
				}

				Set<String> memberNames = zobject.getMemberNames();
				Map<String, Object> members = new HashMap<>(memberNames.size());
				members.put(ZConstants.ZOBJECT_TYPE, type);
				for (String memberName : memberNames) {
					if (!memberName.startsWith("Z1K")) {
						members.put(memberName, zobject.readMember(memberName));
					}
				}
				return new ZObject(members);
			} catch (UnknownIdentifierException e) {
				// This should never happen; we only read keys that are guaranteed to be present
				throw new RuntimeException(e);
			}
		}

		return object;
	}

}
