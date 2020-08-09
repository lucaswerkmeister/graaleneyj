package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.ZConstants;

public class BuiltinTest extends ZTest {

	@Test
	public void testValueOfProjectNameLocalKeys() {
		assertEquals("eneyj", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z28\"}").asString());
	}

	@Test
	public void testValueOfProjectNameGlobalKeys() {
		assertEquals("eneyj", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"Z36K1\": \"Z28\"}").asString());
	}

	@Test
	public void testValueOfStringLiteral() {
		assertEquals("a string", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"a string\"}").asString());
	}

	@Test
	public void testValueOfStringObject() {
		assertEquals("another string", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": "
				+ "{\"Z1K1\": \"Z6\", \"Z6K1\": \"another string\"}" + "}").asString());
	}

	@Test
	public void testValueOfBooleanLiteral() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z54\"}").asBoolean());
	}

	@Test
	public void testValueOfBooleanObject() {
		assertEquals(true,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + "{\"Z1K1\": \"Z50\", \"Z50K1\": \"Z54\"}" + "}")
						.asBoolean());
	}

	@Test
	public void testValueOfPairWithoutId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": {\"Z1K1\": \"Z2\", \"Z2K1\": \"first\", \"Z2K2\": \"second\"}}");
		assertEquals("first", result.getMember("Z2K1").asString());
		assertEquals("second", result.getMember("Z2K2").asString());
		assertFalse(result.hasMember("Z1K2"));
	}

	@Test
	public void testValueOfPairWithId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": {\"Z1K1\": \"Z2\", \"Z1K2\": \"Z0\", \"Z2K1\": \"first\", \"Z2K2\": \"second\"}}");
		assertEquals("first", result.getMember("Z2K1").asString());
		assertEquals("second", result.getMember("Z2K2").asString());
		assertFalse(result.hasMember("Z1K2"));
	}

	@Test
	public void testValueOfCharacterAWithoutId() {
		String characterAWithoutId = "{\"Z1K1\": \"Z60\", \"Z60K1\": \"A\"}";
		assertEquals("A",
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + characterAWithoutId + "}").asString());
	}

	@Test
	public void testValueOfCharacterAWithId() {
		String characterAWithoutId = "{\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"A\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + characterAWithoutId + "}");
		assertEquals("A", result.asString());
		assertNull(result.getMember("Z1K2"));
	}

	@Test
	public void testValueOfCharacterThinkingFaceWithId() {
		String characterThinkingFaceWithoutId = "{\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"🤔\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + characterThinkingFaceWithoutId + "}");
		assertEquals("🤔", result.asString());
		assertNull(result.getMember("Z1K2"));
	}

	@Test
	public void testHeadOfSingleElementList() {
		assertEquals("A", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z64\", \"K1\": [\"A\"]}").asString());
	}

	@Test
	public void testHeadOfTwoElementsList() {
		assertEquals("A", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z64\", \"K1\": [\"A\", \"B\"]}").asString());
	}

	@Test
	public void testHeadOfNil() {
		try {
			eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z64\", \"K1\": []}");
			throw new IllegalStateException("Should have thrown an exception");
		} catch (PolyglotException e) {
			assertTrue(e.isGuestException());
			assertEquals(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2").toString());
		}
	}

	@Test
	public void testTailOfSingleElementList() {
		assertEquals("[]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z65\", \"K1\": [\"A\"]}").toString());
	}

	@Test
	public void testTailOfTwoElementList() {
		assertEquals("[B]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z65\", \"K1\": [\"A\", \"B\"]}").toString());
	}

	@Test
	public void testTailOfThreeElementList() {
		assertEquals("[B, C]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z65\", \"K1\": [\"A\", \"B\", \"C\"]}").toString());
	}

	@Test
	public void testTailOfFourElementList() {
		assertEquals("[B, C, D]",
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z65\", \"K1\": [\"A\", \"B\", \"C\", \"D\"]}").toString());
	}

	@Test
	public void testTailOfNil() {
		try {
			eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z65\", \"K1\": []}");
			throw new IllegalStateException("Should have thrown an exception");
		} catch (PolyglotException e) {
			assertTrue(e.isGuestException());
			assertEquals(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2").toString());
		}
	}

	@Test
	public void testTailOfSingleElementArray() {
		assertEquals("[]", eval("Z65").execute().execute((Object) new String[] { "A" }).toString());
	}

	@Test
	public void testTailOfTwoElementArray() {
		assertEquals("[B]", eval("Z65").execute().execute((Object) new String[] { "A", "B" }).toString());
	}

	@Test
	public void testTailOfThreeElementArray() {
		assertEquals("[B, C]", eval("Z65").execute().execute((Object) new String[] { "A", "B", "C" }).toString());
	}

	@Test
	public void testTailOfFourElementArray() {
		assertEquals("[B, C, D]",
				eval("Z65").execute().execute((Object) new String[] { "A", "B", "C", "D" }).toString());
	}

	@Test
	public void testTailOfEmptyArray() {
		try {
			eval("Z65").execute().execute((Object) new String[0]).toString();
			throw new IllegalStateException("Should have thrown an exception");
		} catch (PolyglotException e) {
			assertTrue(e.isGuestException());
			assertEquals(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2").toString());
		}
	}

	@Test
	public void testSameTrueTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z54\", \"K2\": \"Z54\"}").asBoolean());
	}

	@Test
	public void testSameTrueFalse() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z54\", \"K2\": \"Z55\"}").asBoolean());
	}

	@Test
	public void testSameTrueString() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z54\", \"K2\": \"true\"}").asBoolean());
	}

	@Test
	public void testSameTrueObjectTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z54\", \"K2\": "
				+ "{\"Z1K1\": \"Z50\", \"Z50K1\": \"Z54\"}}").asBoolean());
	}

	@Test
	public void testSameSameString() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"s\", \"K2\": \"s\"}").asBoolean());
	}

	@Test
	public void testSameDifferentStrings() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"s\", \"K2\": \"t\"}").asBoolean());
	}

	@Test
	public void testSameProjectnameEneyj() {
		assertEquals(true,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z28\", \"K2\": \"eneyj\"}").asBoolean());
	}

	@Test
	public void testSameSameObject() {
		assertEquals(true,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": {\"Z1K1\": \"Z1\"}, \"K2\": {\"Z1K1\": \"Z1\"}}")
						.asBoolean());
	}

	@Test
	public void testSameSameObjectButWithId() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": {\"Z1K1\": \"Z1\"}, "
				+ "\"K2\": {\"Z1K1\": \"Z1\", \"Z1K2\": \"Z10000\"}}").asBoolean());
	}

	@Test
	public void testSameDifferentObjects() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": {\"Z1K1\": \"Z10000\", \"Z10000K1\": \"a\"}, "
						+ "\"K2\": {\"Z1K1\": \"Z10000\", \"Z10000K1\": \"b\"}}").asBoolean());
	}

	@Test
	public void testSameNilNil() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": [], \"K2\": []}").asBoolean());
	}

	@Test
	public void testSameNilEmptyString() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": [], \"K2\": \"\"}").asBoolean());
	}

	@Test
	public void testSameNilNonemptyList() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": [], \"K2\": [\"\"]}").asBoolean());
	}

	@Test
	public void testSameSameNonemptyList() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": [\"\"], \"K2\": [\"\"]}").asBoolean());
	}

	@Test
	public void testSameHeadHead() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z64\", \"K2\": \"Z64\"}").asBoolean());
	}

	@Test
	public void testSameHeadTail() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z64\", \"K2\": \"Z65\"}").asBoolean());
	}

	/**
	 * A version of Z61/character_to_string with only the builtin implementation.
	 */
	private String characterToStringWithOnlyBuiltinImplementation() {
		return "{\"Z1K1\": \"Z8\", \"Z1K2\": \"Z61\", \"Z8K1\": [{\"Z1K2\": \"Z61K1\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": {\"Z1K1\": \"Z19\", \"Z1K2\": \"Z61\"}}]}";
	}

	@Test
	public void testCharacterToStringOfA() {
		assertEquals("A", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + characterToStringWithOnlyBuiltinImplementation()
				+ ", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"A\"}}").asString());
	}

	@Test
	public void testCharacterToStringOfAe() {
		assertEquals("Ä", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + characterToStringWithOnlyBuiltinImplementation()
				+ ", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"Ä\"}}").asString());
	}

	@Test
	public void testCharacterToStringOfAlpha() {
		assertEquals("α", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + characterToStringWithOnlyBuiltinImplementation()
				+ ", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"α\"}}").asString());
	}

	@Test
	public void testCharacterToStringOfThinkingFace() {
		assertEquals("🤔", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + characterToStringWithOnlyBuiltinImplementation()
				+ ", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"🤔\"}}").asString());
	}

	@Test
	public void testStringToCharacterlistOfEmptyString() {
		assertEquals("[]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z62\", \"K1\": \"\"}").toString());
	}

	@Test
	public void testStringToCharacterlistOfSingleCharacterString() {
		assertEquals("[A]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z62\", \"K1\": \"A\"}").toString());
	}

	@Test
	public void testStringToCharacterlistOfTwoCharacterString() {
		assertEquals("[A, B]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z62\", \"K1\": \"AB\"}").toString());
	}

	@Test
	public void testStringToCharacterlistOfNonBmpString() {
		assertEquals("[🥳, 🎉]", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z62\", \"K1\": \"🥳🎉\"}").toString());
	}

	// Z37/reify tests; note that Z37/reify does not guarantee any result order,
	// which makes the assertions a bit cumbersome

	@Test
	public void testReifyObject() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z1\", \"Z1K2\": \"Z0\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z2K1").asString().equals("Z1K2")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z2K1").asString());
		assertEquals("Z1", first.getMember("Z2K2").toString());
		assertEquals("Z1K2", second.getMember("Z2K1").asString());
		assertEquals("Z0", second.getMember("Z2K2").toString());
	}

	@Test
	public void testReifyCharacterWithoutId() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z2K1").asString().equals("Z60K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z2K1").asString());
		assertEquals("Z60", first.getMember("Z2K2").toString());
		assertEquals("Z60K1", second.getMember("Z2K1").asString());
		assertEquals("a", second.getMember("Z2K2").asString());
	}

	@Test
	public void testReifyCharacterWithId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"a\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(3, result.getArraySize());
		boolean sawType = false, sawId = false, sawCharacter = false;
		for (int i = 0; i < 3; i++) {
			Value value = result.getArrayElement(i);
			switch (value.getMember("Z2K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertEquals("Z60", value.getMember("Z2K2").toString());
				break;
			case "Z1K2":
				assertFalse(sawId);
				sawId = true;
				assertEquals("Z0", value.getMember("Z2K2").toString());
				break;
			case "Z60K1":
				assertFalse(sawCharacter);
				sawCharacter = true;
				assertEquals("a", value.getMember("Z2K2").asString());
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawId);
		assertTrue(sawCharacter);
	}

	@Test
	public void testReifyStringLiteral() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"a string\"}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z2K1").asString().equals("Z6K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z2K1").asString());
		assertEquals("Z6", first.getMember("Z2K2").toString());
		assertEquals("Z6K1", second.getMember("Z2K1").asString());
		assertEquals("a string", second.getMember("Z2K2").asString());
	}

	@Test
	public void testReifyStringWithoutId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"a string\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z2K1").asString().equals("Z6K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z2K1").asString());
		assertEquals("Z6", first.getMember("Z2K2").toString());
		assertEquals("Z6K1", second.getMember("Z2K1").asString());
		assertEquals("a string", second.getMember("Z2K2").asString());
	}

	@Test
	public void testReifyStringWithId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a string\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(3, result.getArraySize());
		boolean sawType = false, sawId = false, sawString = false;
		for (int i = 0; i < 3; i++) {
			Value value = result.getArrayElement(i);
			switch (value.getMember("Z2K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertEquals("Z6", value.getMember("Z2K2").toString());
				break;
			case "Z1K2":
				assertFalse(sawId);
				sawId = true;
				assertEquals("Z0", value.getMember("Z2K2").toString());
				break;
			case "Z6K1":
				assertFalse(sawString);
				sawString = true;
				assertEquals("a string", value.getMember("Z2K2").asString());
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawId);
		assertTrue(sawString);
	}

}
