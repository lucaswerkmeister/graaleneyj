package de.lucaswerkmeister.graaleneyj;

/**
 * Names of certain well-known objects and keys.
 */
public final class ZConstants {

	public static final String FUNCTIONCALL = "Z7";
	public static final String FUNCTION = "Z8";
	public static final String NIL = "Z13";
	public static final String BUILTIN = "Z19";
	public static final String VALUE = "Z36";
	public static final String TRUE = "Z54";
	public static final String FALSE = "Z55";

	public static final String ZOBJECT_TYPE = "Z1K1";
	public static final String ZOBJECT_ID = "Z1K2";
	public static final String FUNCTIONCALL_FUNCTION = "Z7K1";
	public static final String FUNCTION_IMPLEMENTATIONS = "Z8K4";
	public static final String IMPLEMENTATION_IMPLEMENTATION = "Z14K1";

	private ZConstants() {
		// disable instantiation, this is just a container for constants
	}

}
