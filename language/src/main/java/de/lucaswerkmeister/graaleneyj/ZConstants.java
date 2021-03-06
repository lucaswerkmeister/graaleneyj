package de.lucaswerkmeister.graaleneyj;

/**
 * Names of certain well-known objects and keys.
 */
public final class ZConstants {

	public static final String ZOBJECT = "Z1";
	public static final String PERSISTENTOBJECT = "Z2";
	public static final String TYPE = "Z4";
	public static final String STRING = "Z6";
	public static final String FUNCTIONCALL = "Z7";
	public static final String FUNCTION = "Z8";
	public static final String REFERENCE = "Z9";
	public static final String LIST = "Z10";
	public static final String MONOLINGUALTEXT = "Z11";
	public static final String MULTILINGUALTEXT = "Z12";
	public static final String NIL = "Z13";
	public static final String IMPLEMENTATION = "Z14";
	public static final String CODE = "Z16";
	public static final String PARAMETER = "Z17";
	public static final String ARGUMENTREFERENCE = "Z18";
	public static final String PAIR = "Z22";
	public static final String NOTHING = "Z23";
	public static final String SAME = "Z33";
	public static final String VALUE = "Z36";
	public static final String REIFY = "Z37";
	public static final String ABSTRACT = "Z38";
	public static final String BOOLEAN = "Z40";
	public static final String TRUE = "Z41";
	public static final String FALSE = "Z42";
	public static final String CHARACTER = "Z60";
	public static final String CHARACTERTOSTRING = "Z61";
	public static final String STRINGTOCHARACTERLIST = "Z62";
	public static final String HEAD = "Z64";
	public static final String TAIL = "Z65";
	public static final String LISTISNIL = "Z441";
	public static final String IF = "Z802";

	public static final String ZOBJECT_TYPE = "Z1K1";
	public static final String PERSISTENTOBJECT_ID = "Z2K1";
	public static final String PERSISTENTOBJECT_VALUE = "Z2K2";
	public static final String PERSISTENTOBJECT_LABEL = "Z2K3";
	public static final String TYPE_IDENTITY = "Z4K1";
	public static final String STRING_STRING_VALUE = "Z6K1";
	public static final String FUNCTIONCALL_FUNCTION = "Z7K1";
	public static final String FUNCTION_ARGUMENTS = "Z8K1";
	public static final String FUNCTION_IMPLEMENTATIONS = "Z8K4";
	public static final String FUNCTION_IDENTITY = "Z8K5";
	public static final String REFERENCE_ID = "Z9K1";
	public static final String LIST_HEAD = "Z10K1";
	public static final String LIST_TAIL = "Z10K2";
	public static final String MONOLINGUALTEXT_LANGUAGE = "Z11K1";
	public static final String MONOLINGUALTEXT_TEXT = "Z11K2";
	public static final String MULTILINGUALTEXT_TEXTS = "Z12K1";
	public static final String IMPLEMENTATION_IMPLEMENTS = "Z14K1";
	public static final String IMPLEMENTATION_FUNCTIONCALL = "Z14K2";
	public static final String IMPLEMENTATION_CODE = "Z14K3";
	public static final String IMPLEMENTATION_BUILTIN = "Z14K4";
	public static final String CODE_LANGUAGE = "Z16K1";
	public static final String CODE_SOURCE = "Z16K2";
	public static final String PARAMETER_KEYID = "Z17K2";
	public static final String ARGUMENTREFERENCE_REFERENCE = "Z18K1";
	public static final String PAIR_FIRST = "Z22K1";
	public static final String PAIR_SECOND = "Z22K2";
	public static final String BOOLEAN_IDENTITY = "Z40K1";
	public static final String CHARACTER_CHARACTER = "Z60K1";

	private ZConstants() {
		// disable instantiation, this is just a container for constants
	}

}
