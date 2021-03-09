package de.lucaswerkmeister.graaleneyj.builtins;

import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.runtime.ZContext;
import de.lucaswerkmeister.graaleneyj.runtime.ZErrorException;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;

/**
 * The Z65/tail function returns the “tail” of a list, that is, a list with all
 * the original list’s elements save the first one. The input list must be
 * nonempty; if it is nil, an error Z441/list_is_nil is thrown.
 */
@NodeInfo(shortName = "tail")
public abstract class ZTailBuiltin extends ZBuiltinNode {

	protected static boolean isNil(ZList list) {
		return list == ZList.NIL;
	}

	protected static boolean isEmpty(Object list, InteropLibrary lists) {
		try {
			return lists.getArraySize(list) <= 0;
		} catch (UnsupportedMessageException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get the tail of a non-nil {@ZList} by returning its existing tail list.
	 */
	@Specialization(guards = { "!isNil(list)" })
	public ZList getTailOfZList(ZList list) {
		return list.getTail();
	}

	/**
	 * Get the tail of nil by throwing a “list is nil” error.
	 */
	@Specialization(guards = { "isNil(list)" })
	public ZList getTailOfNil(ZList list, @CachedContext(ZLanguage.class) ZContext context) {
		throw new ZErrorException(context.loadError(ZConstants.LISTISNIL), this);
	}

	/**
	 * Get the tail of any nonempty array-like interop object by building a new
	 * {@link ZList}, starting with nil and adding all the array elements from the
	 * back save the first one.
	 */
	@Specialization(guards = { "lists.hasArrayElements(list)",
			"!isEmpty(list, lists)" }, replaces = "getTailOfZList", limit = "3")
	public ZList getTailOfNonempty(Object list, @CachedLibrary("list") InteropLibrary lists) {
		try {
			long i = lists.getArraySize(list) - 1;
			if (i < 0) {
				throw new IllegalStateException("array size was > 0 in guard");
			}
			ZList ret = ZList.NIL;
			while (i > 0) {
				ret = new ZList(lists.readArrayElement(list, i), ret);
				i--;
			}
			return ret;
		} catch (UnsupportedMessageException | InvalidArrayIndexException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get the tail of any empty array-like interop object by throwing a “list is
	 * nil” error.
	 */
	@Specialization(guards = { "lists.hasArrayElements(list)",
			"isEmpty(list, lists)" }, replaces = "getTailOfNil", limit = "3")
	public ZList getTailOfEmpty(Object list, @CachedLibrary("list") InteropLibrary lists,
			@CachedContext(ZLanguage.class) ZContext context) {
		throw new ZErrorException(context.loadError(ZConstants.LISTISNIL), this);
	}

}
