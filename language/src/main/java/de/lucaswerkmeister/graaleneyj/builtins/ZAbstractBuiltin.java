package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;

import de.lucaswerkmeister.graaleneyj.nodes.ZAbstractNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZAbstractNodeGen;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;

/**
 * The Z38/abstract builtin, a list of key / reified value pairs into an object.
 * Most of the work happens in {@link ZAbstractNode}.
 */
public abstract class ZAbstractBuiltin extends ZBuiltinNode {

	@Child
	private ZAbstractNode abstract_ = ZAbstractNodeGen.create();

	@Specialization
	public Object doList(ZList list) {
		return abstract_.execute(list);
	}

	// TODO error fallback for non-list arguments

}
