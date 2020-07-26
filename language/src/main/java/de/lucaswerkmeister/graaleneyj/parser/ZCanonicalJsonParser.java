package de.lucaswerkmeister.graaleneyj.parser;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.oracle.truffle.api.dsl.NodeFactory;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.builtins.ZHeadBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZSameBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZTailBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltinFactory;
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
		case ZConstants.FUNCTIONCALL:
			return parseJsonObjectAsFunctionCall(json);
		case ZConstants.FUNCTION:
			return parseJsonObjectAsFunction(json);
		case ZConstants.ARGUMENTREFERENCE:
			return parseJsonObjectAsArgumentReference(json);
		}
		ZObjectLiteralMemberNode[] members = new ZObjectLiteralMemberNode[json.size()];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			members[i] = new ZObjectLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZObjectLiteralNode(members);
	}

	public ZNode parseJsonObjectAsFunctionCall(JsonObject json) {
		// TODO error handling, and check whether it’s okay to throw away all other keys
		ZNode function = parseJsonElement(json.get(ZConstants.FUNCTIONCALL_FUNCTION));
		String functionName = "";
		if (function instanceof ZReferenceLiteralNode) {
			functionName = ((ZReferenceLiteralNode) function).getId();
		}
		ArrayList<ZNode> arguments = new ArrayList<ZNode>();
		for (int i = 1; json.has("K" + i) || json.has(functionName + "K" + i); i++) {
			// TODO complain if both "K" + i and functionName + "K" + i are set
			arguments.add(parseJsonElement(json.has("K" + i) ? json.get("K" + i) : json.get(functionName + "K" + i)));
		}
		if (ZConstants.IF.equals(functionName)) {
			assert arguments.size() == 3;
			return new ZIfNode(arguments.get(0), arguments.get(1), arguments.get(2));
		}
		return new ZFunctionCallNode(function, arguments.toArray(new ZNode[arguments.size()]));
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

	public ZReadArgumentNode parseJsonObjectAsArgumentReference(JsonObject json) {
		// TODO is it safe to assume that ZwhateverKi is always the (i-1)th argument?
		String reference = json.get(ZConstants.ARGUMENTREFERENCE_REFERENCE).getAsString();
		int index = Integer.parseInt(reference.substring(reference.indexOf('K') + 1));
		return new ZReadArgumentNode(index - 1);
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
