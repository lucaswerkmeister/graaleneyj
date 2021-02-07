package de.lucaswerkmeister.graaleneyj;

/**
 * Names of certain well-known objects and keys.
 */
public final class ZConstants {

	public static final String ZOBJECT = "Z1";
	public static final String STRING = "Z6";
	public static final String FUNCTIONCALL = "Z7";
	public static final String FUNCTION = "Z8";
	public static final String REFERENCE = "Z9";
	public static final String LIST = "Z10";
	public static final String NIL = "Z13";
	public static final String CODE = "Z16";
	public static final String ARGUMENTREFERENCE = "Z18";
	public static final String BUILTIN = "Z19";
	public static final String PAIR = "Z22";
	public static final String NOTHING = "Z23";
	public static final String IF = "Z31";
	public static final String SAME = "Z33";
	public static final String VALUE = "Z36";
	public static final String REIFY = "Z37";
	public static final String ABSTRACT = "Z38";
	public static final String BOOLEAN = "Z50";
	public static final String TRUE = "Z54";
	public static final String FALSE = "Z55";
	public static final String CHARACTER = "Z60";
	public static final String CHARACTERTOSTRING = "Z61";
	public static final String STRINGTOCHARACTERLIST = "Z62";
	public static final String HEAD = "Z64";
	public static final String TAIL = "Z65";
	public static final String LISTISNIL = "Z441";

	public static final String ZOBJECT_TYPE = "Z1K1";
	public static final String ZOBJECT_ID = "Z1K2";
	public static final String STRING_STRING_VALUE = "Z6K1";
	public static final String FUNCTIONCALL_FUNCTION = "Z7K1";
	public static final String FUNCTION_ARGUMENTS = "Z8K1";
	public static final String FUNCTION_IMPLEMENTATIONS = "Z8K4";
	public static final String REFERENCE_ID = "Z9K1";
	public static final String LIST_HEAD = "Z10K1";
	public static final String LIST_TAIL = "Z10K2";
	public static final String IMPLEMENTATION_IMPLEMENTATION = "Z14K1";
	public static final String CODE_LANGUAGE = "Z16K1";
	public static final String CODE_SOURCE = "Z16K2";
	public static final String ARGUMENTREFERENCE_REFERENCE = "Z18K1";
	public static final String PAIR_FIRST = "Z22K1";
	public static final String PAIR_SECOND = "Z22K2";
	public static final String BOOLEAN_IDENTITY = "Z50K1";
	public static final String CHARACTER_CHARACTER = "Z60K1";

	private ZConstants() {
		// disable instantiation, this is just a container for constants
	}

}
