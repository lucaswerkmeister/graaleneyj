package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import de.lucaswerkmeister.graaleneyj.runtime.ZList;

@TypeSystemReference(ZTypes.class)
@NodeInfo(language = "Z language", description = "The abstract base node.") // TODO Z language? Eneyj language?
public abstract class ZNode extends Node {

	private int sourceCharIndex = -1;
	private int sourceLength = -1;

	public abstract Object execute(VirtualFrame virtualFrame);

	public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectBoolean(execute(virtualFrame));
	}

	public int executeCharacter(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectInteger(execute(virtualFrame));
	}

	public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectString(execute(virtualFrame));
	}

	public ZList executeZList(VirtualFrame virtualFrame) throws UnexpectedResultException {
		return ZTypesGen.expectZList(execute(virtualFrame));
	}

	public void setSourceSection(int sourceCharIndex, int sourceLength) {
		assert this.sourceCharIndex == -1;
		assert this.sourceLength == -1;
		this.sourceCharIndex = sourceCharIndex;
		this.sourceLength = sourceLength;
	}

	public int getSourceCharIndex() {
		return sourceCharIndex;
	}

	public int getSourceLength() {
		return sourceLength;
	}

	@Override
	public SourceSection getSourceSection() {
		if (sourceCharIndex == -1) {
			return null;
		}
		RootNode rootNode = getRootNode();
		if (rootNode == null) {
			return null;
		}
		Source source = rootNode.getSourceSection().getSource();
		return source.createSection(sourceCharIndex, sourceLength);
	}

}
