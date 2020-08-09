package de.lucaswerkmeister.graaleneyj.nodes;

import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

import de.lucaswerkmeister.graaleneyj.runtime.ZCharacter;
import de.lucaswerkmeister.graaleneyj.runtime.ZList;
import de.lucaswerkmeister.graaleneyj.runtime.ZNothing;
import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZReference;

@TypeSystem({
		// Z50/boolean (Z54/true and Z55/false)
		boolean.class,
		// Z60/character (int, not char, to fully represent Unicode code points)
		int.class,
		// Z6/string
		String.class,
		// Z9/reference
		ZReference.class,
		// Z10/list
		ZList.class,
		// Z60/character, boxed
		ZCharacter.class,
		// Z1/zobject (not sure if this needs to be in the type system at all)
		ZObject.class })
public abstract class ZTypes {

	/**
	 * Manually implemented type check replacing the Truffle-generated one: as there
	 * is only a {@link ZNothing#SINGLETON singleton} instance, we can skip an
	 * {@code instanceof} check.
	 */
	@TypeCheck(ZNothing.class)
	public static boolean isZNothing(Object value) {
		return value == ZNothing.INSTANCE;
	}

	/**
	 * Manually implemented type cast replacing the Truffle-generated one: as there
	 * is only a {@link ZNothing#SINGLETON singleton} instance, we can skip an
	 * actual cast.
	 */
	@TypeCast(ZNothing.class)
	public static ZNothing asZNothing(Object value) {
		assert isZNothing(value);
		return ZNothing.INSTANCE;
	}

	@ImplicitCast
	public static ZCharacter intToZCharacter(int character) {
		return ZCharacter.cast(character);
	}

}
