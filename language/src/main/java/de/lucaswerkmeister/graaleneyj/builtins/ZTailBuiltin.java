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

	/**
	 * Get the tail of a {@ZList} by returning its existing tail list.
	 */
	@Specialization
	public ZList getTailOfZList(ZList list, @CachedContext(ZLanguage.class) ZContext context) {
		if (list == ZList.NIL) {
			throw new ZErrorException(context.loadError(ZConstants.LISTISNIL), this);
		} else {
			return list.getTail();
		}
	}

	/**
	 * Get the tail of any array-like interop object by building a new
	 * {@link ZList}, starting with nil and adding all the array elements from the
	 * back save the first one.
	 */
	@Specialization(limit = "3")
	public ZList getTail(Object list, @CachedLibrary("list") InteropLibrary lists,
			@CachedContext(ZLanguage.class) ZContext context) {
		try {
			long i = lists.getArraySize(list) - 1;
			if (i < 0) {
				throw new ZErrorException(context.loadError(ZConstants.LISTISNIL), this);
			}
			ZList ret = ZList.NIL;
			while (i > 0) {
				ret = new ZList(lists.readArrayElement(list, i), ret);
				i--;
			}
			return ret;
		} catch (UnsupportedMessageException e) {
			throw new IllegalArgumentException(e); // TODO throw right error
		} catch (InvalidArrayIndexException e) {
			throw new IllegalStateException(e);
		}
	}

}
