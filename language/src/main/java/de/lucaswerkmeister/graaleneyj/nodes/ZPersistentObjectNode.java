package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZPersistentObject;

public abstract class ZPersistentObjectNode extends ZNode {

	private final String id;

	@Child
	private ZNode value;

	@Child
	private ZNode labels;

	public ZPersistentObjectNode(String id, ZNode value, ZNode labels) {
		this.id = id;
		this.value = value;
		this.labels = labels;
	}

	@Specialization
	public Object executeGeneric(VirtualFrame virtualFrame, @CachedContext(ZLanguage.class) ZContext context) {
		Object value = this.value.execute(virtualFrame);
		Object labels = null;
		if (this.labels != null) {
			labels = this.labels.execute(virtualFrame);
		}
		return new ZPersistentObject(id, value, labels, context.getInitialZObjectShape());
	}

}
