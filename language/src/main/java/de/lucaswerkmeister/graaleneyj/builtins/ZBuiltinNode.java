package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeChild;

import de.lucaswerkmeister.graaleneyj.nodes.ZNode;

@NodeChild(value = "arguments", type = ZNode[].class)
@GenerateNodeFactory
public abstract class ZBuiltinNode extends ZNode {

}
