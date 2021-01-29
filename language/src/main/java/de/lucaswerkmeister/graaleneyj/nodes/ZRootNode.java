package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

/**
 * Wraps any other node, since any value may be evaluated (to itself, if nothing
 * else).
 */
public final class ZRootNode extends RootNode {

	@Child
	private ZNode node;

	private final SourceSection sourceSection;

	public ZRootNode(ZLanguage language, ZNode node, SourceSection sourceSection) {
		super(language);
		this.node = node;
		this.sourceSection = sourceSection;
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

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

}
