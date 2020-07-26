package de.lucaswerkmeister.graaleneyj.nodes;

import java.util.Arrays;
import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.nodes.Node;

/**
 * Hand-written factory for ZIfNode (based on ZSameBuiltinFactory), so that the
 * node can also be treated like a builtin function.
 */
public class ZIfNodeFactory implements NodeFactory<ZIfNode> {

	private static ZIfNodeFactory instance;

	@Override
	public Class<ZIfNode> getNodeClass() {
		return ZIfNode.class;
	}

	@Override
	public List<Class<? extends Node>> getExecutionSignature() {
		return Arrays.asList(ZNode.class, ZNode.class, ZNode.class);
	}

	@Override
	public List<List<Class<?>>> getNodeSignatures() {
		return Arrays.asList(Arrays.asList(ZNode[].class));
	}

	@Override
	public ZIfNode createNode(Object... arguments) {
		if (arguments.length == 1 && (arguments[0] == null || arguments[0] instanceof ZNode[])) {
			return create((ZNode[]) arguments[0]);
		} else {
			throw new IllegalArgumentException("Invalid create signature.");
		}
	}

	public static NodeFactory<ZIfNode> getInstance() {
		if (instance == null) {
			instance = new ZIfNodeFactory();
		}
		return instance;
	}

	public static ZIfNode create(ZNode[] arguments) {
		assert arguments.length == 3;
		return new ZIfNode(arguments[0], arguments[1], arguments[2]);
	}

}
