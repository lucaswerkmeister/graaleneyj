package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeSystem;

import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;

@TypeSystem({
		// Z50/boolean (Z54/true and Z55/false)
		boolean.class,
		// Z60/character (int, not char, to fully represent Unicode code points)
		int.class,
		// Z6/string
		String.class,
		// Z10/list
		ZList.class,
		// Z60/character, boxed
		ZCharacter.class,
		// Z1/zobject (not sure if this needs to be in the type system at all)
		ZObject.class })
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

	@ImplicitCast
	public static ZCharacter intToZCharacter(int character) {
		return ZCharacter.cast(character);
	}

}
