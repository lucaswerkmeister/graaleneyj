package de.lucaswerkmeister.graaleneyj.parser;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.oracle.truffle.api.dsl.NodeFactory;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.builtins.ZAbstractBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZCharacterToStringBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZHeadBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZReifyBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZSameBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZStringToCharacterlistFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZTailBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.nodes.ZCharacterLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZCharacterLiteralNode.ZCharacterLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionCallNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZIfNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZIfNodeFactory;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationBuiltinNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationCodeNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationFunctioncallNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZListLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode.ZObjectLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReadArgumentNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNodeGen;
import de.lucaswerkmeister.graaleneyj.nodes.ZRootNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZStringLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZStringLiteralNode.ZStringLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZThrowConstantNode;
import de.lucaswerkmeister.graaleneyj.runtime.UnusableImplementationException;

public class ZCanonicalJsonParser {

	private final ZLanguage language;

	public ZCanonicalJsonParser(ZLanguage language) {
		this.language = language;
	}

	public ZNode parseJsonElement(JsonElement json) {
		if (json instanceof JsonObject) {
			return parseJsonObject((JsonObject) json);
		}
		if (json instanceof JsonArray) {
			return parseJsonArray((JsonArray) json);
		}
		if (json instanceof JsonPrimitive) {
			JsonPrimitive primitive = (JsonPrimitive) json;
			if (primitive.isString()) {
				return parseJsonString(primitive.getAsString());
			} else {
				throw new IllegalArgumentException("JSON literal must be string");
			}
		}
		throw new IllegalStateException("JSON element was neither object nor array nor primitive");
	}

	public ZNode parseJsonObject(JsonObject json) {
		String type = json.get(ZConstants.ZOBJECT_TYPE).getAsString(); // TODO error handling
		switch (type) {
		case ZConstants.STRING:
			return parseJsonObjectAsStringLiteral(json);
		case ZConstants.FUNCTIONCALL:
			return parseJsonObjectAsFunctionCall(json);
		case ZConstants.FUNCTION:
			return parseJsonObjectAsFunction(json);
		case ZConstants.REFERENCE:
			return parseJsonObjectAsReference(json);
		case ZConstants.ARGUMENTREFERENCE:
			return parseJsonObjectAsArgumentReference(json);
		case ZConstants.CHARACTER:
			return parseJsonObjectAsCharacter(json);
		}
		ZObjectLiteralMemberNode[] members = new ZObjectLiteralMemberNode[json.size()];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			members[i] = new ZObjectLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZObjectLiteralNode(members);
	}

	/**
	 * Parse a JSON object as a string literal. This is used when the string value
	 * would otherwise look like a reference, or if the string has extra members.
	 */
	public ZNode parseJsonObjectAsStringLiteral(JsonObject json) {
		ZStringLiteralMemberNode[] extraMembers = new ZStringLiteralMemberNode[json.size() - 2];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (ZConstants.ZOBJECT_TYPE.equals(entry.getKey())
					|| ZConstants.STRING_STRING_VALUE.equals(entry.getKey())) {
				continue;
			}
			extraMembers[i] = new ZStringLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZStringLiteralNode(json.get(ZConstants.STRING_STRING_VALUE).getAsString(), extraMembers);
	}

	/**
	 * Parse a JSON object as a function call. Arguments may use global keys (named
	 * arguments) or local keys (positional arguments); additionally, the function
	 * being called may be a reference to another function, and the global keys
	 * might not use the function as the prefix (e. g. the function could be
	 * Z104/if_boolean but the arguments would be Z31K1, Z31K2, Z31K3). We cope with
	 * this by collecting all keys except Z1K* and Z7K* and asserting that they
	 * share the same prefix and are contiguous.
	 */
	public ZNode parseJsonObjectAsFunctionCall(JsonObject json) {
		ZNode function = parseJsonElement(json.get(ZConstants.FUNCTIONCALL_FUNCTION));
		SortedMap<Integer, ZNode> arguments = new TreeMap<>();
		String prefix = null;
		for (String key : json.keySet()) {
			if (key.startsWith(ZConstants.ZOBJECT + "K") || key.startsWith(ZConstants.FUNCTIONCALL + "K")) {
				continue;
			}
			int kIndex = key.indexOf('K');
			if (prefix == null) {
				prefix = key.substring(0, kIndex);
			} else if (!prefix.equals(key.substring(0, kIndex))) {
				throw new IllegalArgumentException("Function call key " + key + " does not share prefix " + prefix
						+ " with other function call keys");
			}
			arguments.put(Integer.parseInt(key.substring(kIndex + 1)), parseJsonElement(json.get(key)));
		}
		if (arguments.firstKey() != 1 || arguments.lastKey() != arguments.size()) {
			throw new IllegalArgumentException("Function call keys are not contiguous: " + arguments.keySet());
		}
		if (function instanceof ZReferenceLiteralNode
				&& ZConstants.IF.equals(((ZReferenceLiteralNode) function).getId())) {
			if (arguments.size() != 3) {
				throw new IllegalArgumentException("Call to if with " + arguments.size() + " ≠ 3 arguments");
			}
			return new ZIfNode(arguments.get(1), arguments.get(2), arguments.get(3));
		}
		return new ZFunctionCallNode(function, arguments.values().toArray(new ZNode[arguments.size()]));
	}

	public ZFunctionNode parseJsonObjectAsFunction(JsonObject json) {
		String functionId = json.get(ZConstants.ZOBJECT_ID).getAsString();
		JsonArray arguments = json.getAsJsonArray(ZConstants.FUNCTION_ARGUMENTS);
		String[] argumentNames = new String[arguments.size()];
		for (int i = 0; i < argumentNames.length; i++) {
			argumentNames[i] = arguments.get(i).getAsJsonObject().get(ZConstants.ZOBJECT_ID).getAsString();
		}
		JsonArray implementationJsons = json.getAsJsonArray(ZConstants.FUNCTION_IMPLEMENTATIONS);
		ZImplementationNode[] implementationNodes = new ZImplementationNode[implementationJsons.size()];
		for (int i = 0; i < implementationNodes.length; i++) {
			implementationNodes[i] = parseJsonObjectAsImplementation(implementationJsons.get(i).getAsJsonObject(),
					functionId, argumentNames);
		}
		return new ZFunctionNode(implementationNodes, functionId);
	}

	public ZImplementationNode parseJsonObjectAsImplementation(JsonObject json, String functionId,
			String[] argumentNames) {
		JsonObject implementation = json.getAsJsonObject(ZConstants.IMPLEMENTATION_IMPLEMENTATION);
		String type = implementation.get(ZConstants.ZOBJECT_TYPE).getAsString();
		switch (type) {
		case ZConstants.FUNCTIONCALL:
			ZNode node = parseJsonObjectAsFunctionCall(implementation);
			ZRootNode rootNode = new ZRootNode(language, node);
			return new ZImplementationFunctioncallNode(rootNode, functionId);
		case ZConstants.BUILTIN:
			String builtin = implementation.get(ZConstants.ZOBJECT_ID).getAsString();
			switch (builtin) {
			case ZConstants.IF:
				// note: parseJsonObjectAsFunctionCall usually parses “if” calls specially, this
				// builtin implementation is used when “if” is called indirectly
				return makeBuiltin(ZIfNodeFactory.getInstance(), functionId);
			case ZConstants.SAME:
				return makeBuiltin(ZSameBuiltinFactory.getInstance(), functionId, ZValueBuiltinFactory.getInstance());
			case ZConstants.VALUE:
				return makeBuiltin(ZValueBuiltinFactory.getInstance(), functionId);
			case ZConstants.REIFY:
				return makeBuiltin(ZReifyBuiltinFactory.getInstance(), functionId);
			case ZConstants.ABSTRACT:
				return makeBuiltin(ZAbstractBuiltinFactory.getInstance(), functionId);
			case ZConstants.CHARACTERTOSTRING:
				return makeBuiltin(ZCharacterToStringBuiltinFactory.getInstance(), functionId);
			case ZConstants.STRINGTOCHARACTERLIST:
				return makeBuiltin(ZStringToCharacterlistFactory.getInstance(), functionId);
			case ZConstants.HEAD:
				return makeBuiltin(ZHeadBuiltinFactory.getInstance(), functionId);
			case ZConstants.TAIL:
				return makeBuiltin(ZTailBuiltinFactory.getInstance(), functionId);
			default:
				return new ZImplementationBuiltinNode(
						new ZRootNode(language,
								new ZThrowConstantNode(
										new UnusableImplementationException("Unknown builtin: " + builtin))),
						functionId);
			}
		case ZConstants.CODE:
			String sourceLanguage = implementation.get(ZConstants.CODE_LANGUAGE).getAsString();
			String source = implementation.get(ZConstants.CODE_SOURCE).getAsString();
			return new ZImplementationCodeNode(language, sourceLanguage, source, functionId, argumentNames);
		default:
			return new ZImplementationBuiltinNode(
					new ZRootNode(language,
							new ZThrowConstantNode(
									new UnusableImplementationException("Unsupported implementation type: " + type))),
					functionId);
		}
	}

	private ZImplementationBuiltinNode makeBuiltin(NodeFactory<? extends ZNode> factory, String functionId) {
		return makeBuiltin(factory, functionId, null);
	}

	/**
	 * Create a builtin-based implementation node for the given function using the
	 * given factory, optionally wrapping each argument in a different builtin.
	 *
	 * @param factory
	 * @param functionId
	 * @param wrapArgumentsFactory Wrap each argument of the outer builtin with a
	 *                             call to this builtin, which must accept a single
	 *                             argument. Used, for instance, by the Z33/same
	 *                             builtin to wrap each argument in a call to the
	 *                             Z36/value builtin.
	 * @return
	 */
	private ZImplementationBuiltinNode makeBuiltin(NodeFactory<? extends ZNode> factory, String functionId,
			NodeFactory<? extends ZNode> wrapArgumentsFactory) {
		int argumentCount = factory.getExecutionSignature().size();
		ZNode[] argumentNodes = new ZNode[argumentCount];
		for (int i = 0; i < argumentCount; i++) {
			ZNode argumentNode;
			if (wrapArgumentsFactory != null) {
				argumentNode = wrapArgumentsFactory.createNode((Object) new ZNode[] { new ZReadArgumentNode(i) });
			} else {
				argumentNode = new ZReadArgumentNode(i);
			}
			argumentNodes[i] = argumentNode;
		}
		ZNode builtinNode = factory.createNode((Object) argumentNodes);
		ZRootNode rootNode = new ZRootNode(language, builtinNode);
		return new ZImplementationBuiltinNode(rootNode, functionId);
	}

	public ZReferenceLiteralNode parseJsonObjectAsReference(JsonObject json) {
		// TODO error handling, and check whether it’s okay to throw away all other keys
		String id = json.get(ZConstants.REFERENCE_ID).getAsString();
		return ZReferenceLiteralNodeGen.create(id);
	}

	public ZReadArgumentNode parseJsonObjectAsArgumentReference(JsonObject json) {
		// TODO is it safe to assume that ZwhateverKi is always the (i-1)th argument?
		String reference = json.get(ZConstants.ARGUMENTREFERENCE_REFERENCE).getAsString();
		int index = Integer.parseInt(reference.substring(reference.indexOf('K') + 1));
		return new ZReadArgumentNode(index - 1);
	}

	public ZCharacterLiteralNode parseJsonObjectAsCharacter(JsonObject json) {
		ZCharacterLiteralMemberNode[] extraMembers = new ZCharacterLiteralMemberNode[json.size() - 2];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (ZConstants.ZOBJECT_TYPE.equals(entry.getKey())
					|| ZConstants.CHARACTER_CHARACTER.equals(entry.getKey())) {
				continue;
			}
			extraMembers[i] = new ZCharacterLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZCharacterLiteralNode(json.get(ZConstants.CHARACTER_CHARACTER).getAsString(), extraMembers);
	}

	public ZListLiteralNode parseJsonArray(JsonArray json) {
		ZNode[] nodes = new ZNode[json.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = parseJsonElement(json.get(i));
		}
		return new ZListLiteralNode(nodes);
	}

	/**
	 * A string represents a reference if it starts with a capital letter followed
	 * by a digit, otherwise it represents a string literal.
	 */
	public ZNode parseJsonString(String json) {
		if (json.length() < 2) {
			return new ZStringLiteralNode(json);
		}
		if (json.charAt(0) > 127 || json.charAt(1) > 127) {
			return new ZStringLiteralNode(json);
		}
		if (!Character.isUpperCase(json.charAt(0))) {
			return new ZStringLiteralNode(json);
		}
		if (!Character.isDigit(json.charAt(1))) {
			return new ZStringLiteralNode(json);
		}
		return ZReferenceLiteralNodeGen.create(json);
	}

}
