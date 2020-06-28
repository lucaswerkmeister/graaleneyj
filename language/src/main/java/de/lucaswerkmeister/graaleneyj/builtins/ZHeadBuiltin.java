package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.runtime.ZErrorException;

@NodeInfo(shortName = "head")
public abstract class ZHeadBuiltin extends ZBuiltinNode {

	// TODO more specializations
	@Specialization(limit = "3")
	public Object getHead(Object list, @CachedLibrary("list") InteropLibrary lists) {
		try {
			return lists.readArrayElement(list, 0);
		} catch (UnsupportedMessageException e) {
			throw new IllegalArgumentException(e); // TODO throw right error
		} catch (InvalidArrayIndexException e) {
			TruffleObject error = null; // TODO load “list is nil” error
			throw new ZErrorException(error, this);
		}
	}

}
