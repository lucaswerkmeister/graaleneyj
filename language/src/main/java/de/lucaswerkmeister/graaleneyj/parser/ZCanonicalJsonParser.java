package de.lucaswerkmeister.graaleneyj.parser;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.lucaswerkmeister.graaleneyj.nodes.ZListLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZObjectLiteralNode.ZObjectLiteralMemberNode;
import de.lucaswerkmeister.graaleneyj.nodes.ZReferenceLiteralNode;
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

	public static ZObjectLiteralNode parseJsonObject(JsonObject json) {
		ZObjectLiteralMemberNode[] members = new ZObjectLiteralMemberNode[json.size()];
		int i = 0;
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			members[i] = new ZObjectLiteralMemberNode(parseJsonString(entry.getKey()),
					parseJsonElement(entry.getValue()));
			i++;
		}
		return new ZObjectLiteralNode(members);
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
