package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * Wraps any other node, since any value may be evaluated (to itself, if nothing
 * else).
 */
public final class ZRootNode extends RootNode {

	private final ZNode node;

	public ZRootNode(ZLanguage language, ZNode node) {
		super(language);
		this.node = node;
	}

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
	protected boolean isInstrumentable() {
		return false;
	}

	@Override
	public String getName() {
		return "root";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return node.execute(virtualFrame);
	}

}
