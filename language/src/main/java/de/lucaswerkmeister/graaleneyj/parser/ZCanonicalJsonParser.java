package de.lucaswerkmeister.graaleneyj.parser;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeFactory;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.builtins.ZBuiltinNode;
import de.lucaswerkmeister.graaleneyj.builtins.ZValueBuiltinFactory;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionCallNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZFunctionNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZImplementationNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZListLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode.ZObjectLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReadArgumentNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZRootNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZStringLiteralNode;

public class ZCanonicalJsonParser {

	public static ZNode parseJsonElement(JsonElement json) {
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

	public static ZNode parseJsonObject(JsonObject json) {
		String type = json.get(ZConstants.ZOBJECT_TYPE).getAsString(); // TODO error handling
		switch (type) {
		case ZConstants.FUNCTIONCALL:
			return parseJsonObjectAsFunctionCall(json);
		case ZConstants.FUNCTION:
			return parseJsonObjectAsFunction(json);
		}
		ZObjectLiteralMemberNode[] members = new ZObjectLiteralMemberNode[json.size()];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			members[i] = new ZObjectLiteralMemberNode(entry.getKey(), parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZObjectLiteralNode(members);
	}

	public static ZFunctionCallNode parseJsonObjectAsFunctionCall(JsonObject json) {
		// TODO error handling, and check whether it’s okay to throw away all other keys
		ZNode function = parseJsonElement(json.get(ZConstants.FUNCTIONCALL_FUNCTION));
		ArrayList<ZNode> arguments = new ArrayList<ZNode>();
		for (int i = 1; json.has("K" + i); i++) {
			arguments.add(parseJsonElement(json.get("K" + i)));
		}
		return new ZFunctionCallNode(function, arguments.toArray(new ZNode[arguments.size()]));
	}

	public static ZFunctionNode parseJsonObjectAsFunction(JsonObject json) {
		JsonArray implementationJsons = json.getAsJsonArray(ZConstants.FUNCTION_IMPLEMENTATIONS);
		ZImplementationNode[] implementationNodes = new ZImplementationNode[implementationJsons.size()];
		for (int i = 0; i < implementationNodes.length; i++) {
			implementationNodes[i] = parseJsonObjectAsImplementation(implementationJsons.get(i).getAsJsonObject());
		}
		return new ZFunctionNode(implementationNodes);
	}

	public static ZImplementationNode parseJsonObjectAsImplementation(JsonObject json) {
		JsonObject implementation = json.getAsJsonObject(ZConstants.IMPLEMENTATION_IMPLEMENTATION);
		String type = implementation.get(ZConstants.ZOBJECT_TYPE).getAsString();
		switch (type) {
		case ZConstants.BUILTIN:
			String builtin = implementation.get(ZConstants.ZOBJECT_ID).getAsString();
			switch (builtin) {
			case ZConstants.VALUE:
				return makeBuiltin(ZValueBuiltinFactory.getInstance());
			default:
				throw new UnsupportedOperationException("Unknown builtin: " + builtin);
			}
		default:
			throw new UnsupportedOperationException("Unsupported implementation type: " + type);
		}
	}

	private static ZImplementationNode makeBuiltin(NodeFactory<? extends ZBuiltinNode> factory) {
		int argumentCount = factory.getExecutionSignature().size();
		ZNode[] argumentNodes = new ZNode[argumentCount];
		for (int i = 0; i < argumentCount; i++) {
			argumentNodes[i] = new ZReadArgumentNode(i);
		}
		ZBuiltinNode builtinNode = factory.createNode((Object) argumentNodes);
		ZRootNode rootNode = new ZRootNode(null, builtinNode); // TODO where does the language come from?
		RootCallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);
		return new ZImplementationNode(callTarget);
	}

	public static ZListLiteralNode parseJsonArray(JsonArray json) {
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
	public static ZNode parseJsonString(String json) {
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
		return new ZReferenceLiteralNode(json);
	}

}
