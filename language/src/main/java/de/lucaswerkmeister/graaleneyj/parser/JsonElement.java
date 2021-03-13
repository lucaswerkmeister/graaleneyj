package de.lucaswerkmeister.graaleneyj.parser;

public abstract class JsonElement {

	private int sourceCharIndex = -1;
	private int sourceLength = -1;

	JsonElement() {
		/* package-private constructor to prevent foreign subclasses */
	}

	void setStartIndex(int startIndex) {
		assert startIndex >= 0;
		assert sourceCharIndex == -1;
		sourceCharIndex = startIndex;
	}

	void setStopIndex(int stopIndex) {
		assert stopIndex > 0;
		assert sourceLength == -1;
		this.sourceLength = stopIndex - sourceCharIndex;
	}

	public int getSourceCharIndex() {
		return sourceCharIndex;
	}

	public int getSourceLength() {
		return sourceLength;
	}

	public JsonObject asJsonObject() {
		throw new IllegalStateException("not an object");
	}

	public String asString() {
		throw new IllegalStateException("not a string");
	}

}
