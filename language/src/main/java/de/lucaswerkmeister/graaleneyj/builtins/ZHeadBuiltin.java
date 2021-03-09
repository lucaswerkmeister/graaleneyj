package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.nodes.ZThrowErrorNode;

@NodeInfo(shortName = "head")
public abstract class ZHeadBuiltin extends ZBuiltinNode {

	protected static boolean isEmpty(Object list, InteropLibrary lists) {
		try {
			return lists.getArraySize(list) <= 0;
		} catch (UnsupportedMessageException e) {
			throw new IllegalStateException(e);
		}
	}

	@Specialization(guards = { "lists.hasArrayElements(list)", "!isEmpty(list, lists)" }, limit = "3")
	public Object getHeadOfNonempty(Object list, @CachedLibrary("list") InteropLibrary lists) {
		try {
			return lists.readArrayElement(list, 0);
		} catch (UnsupportedMessageException | InvalidArrayIndexException e) {
			throw new IllegalStateException(e);
		}
	}

	@Specialization(guards = { "lists.hasArrayElements(list)", "isEmpty(list, lists)" }, limit = "3")
	public void getHeadOfEmpty(Object list, @CachedLibrary("list") InteropLibrary lists,
			@Cached("create()") ZThrowErrorNode throwError) {
		throwError.execute(ZConstants.LISTISNIL);
	}

}
