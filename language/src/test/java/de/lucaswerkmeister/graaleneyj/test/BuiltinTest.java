package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		assertEquals("A",
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + characterAWithoutId + "}").asString());
	}

	@Test
	public void testValueOfCharacterThinkingFaceWithId() {
		String characterThinkingFaceWithoutId = "{\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"🤔\"}";
		assertEquals("🤔", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + characterThinkingFaceWithoutId + "}")
				.asString());
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

	@Test
	public void testCharacterToStringOfA() {
		assertEquals("A", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z61\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"A\"}}")
				.asString());
	}

	@Test
	public void testCharacterToStringOfAe() {
		assertEquals("Ä", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z61\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"Ä\"}}")
				.asString());
	}

	@Test
	public void testCharacterToStringOfAlpha() {
		assertEquals("α", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z61\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"α\"}}")
				.asString());
	}

	@Test
	public void testCharacterToStringOfThinkingFace() {
		assertEquals("🤔", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z61\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"🤔\"}}")
				.asString());
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

}
