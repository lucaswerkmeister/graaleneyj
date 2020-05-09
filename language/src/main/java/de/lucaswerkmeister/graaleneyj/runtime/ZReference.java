package de.lucaswerkmeister.graaleneyj.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ZReference {

	private final String name;

	public ZReference(String name) {
		// TODO check for some pattern?
		this.name = name;
	}

	public static ZReference fromNormalJson(JsonElement json) {
		if (!(json instanceof JsonObject)) {
			throw new IllegalArgumentException("String must be an object");
		}
		JsonObject object = (JsonObject) json;
		throw new IllegalStateException("https://github.com/google/abstracttext/issues/4");
	}

	public static ZReference fromCanonicalJson(JsonElement json) {
		if (!(json instanceof JsonPrimitive)) {
			throw new IllegalArgumentException("Reference must be a literal");
		}
		final JsonPrimitive primitive = (JsonPrimitive) json;
		if (!primitive.isString()) {
			throw new IllegalArgumentException("Reference must be a string literal");
		}
		return new ZReference(primitive.getAsString());
	}

	@Override
	public String toString() {
		return name;
	}

}
