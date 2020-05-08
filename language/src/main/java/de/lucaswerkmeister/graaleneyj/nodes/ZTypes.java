package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

@TypeSystem({
	boolean.class,
	String.class,
	ZList.class,
	ZObject.class,
})
public abstract class ZTypes {

// Commented out because there currently is no such thing as a ZNil type
//	/**
//	 * Manually implemented type check replacing the Truffle-generated one:
//	 * as there is only a {@link ZNil#SINGLETON singleton} instance,
//	 * we can skip an {@code instanceof} check.
//	 */
//	@TypeCheck(ZNil.class)
//	public static boolean isZNil(Object value) {
//		return value == ZNil.SINGLETON;
//	}
//
//	/**
//	 * Manually implemented type cast replacing the Truffle-generated one:
//	 * as there is only a {@link ZNil#SINGLETON singleton} instance,
//	 * we can skip an actual cast.
//	 */
//	@TypeCast(ZNil.class)
//	public static ZNil asZNil(Object value) {
//		assert isZNil(value);
//		return ZNil.SINGLETON;
//	}

}
