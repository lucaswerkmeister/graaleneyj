package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.function.Consumer;

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
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z41\"}").asBoolean());
	}

	@Test
	public void testValueOfBooleanObject() {
		assertEquals(true,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": " + "{\"Z1K1\": \"Z40\", \"Z40K1\": \"Z41\"}" + "}")
						.asBoolean());
	}

	@Test
	public void testValueOfPairWithoutId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": {\"Z1K1\": \"Z22\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}}");
		assertEquals("first", result.getMember("Z22K1").asString());
		assertEquals("second", result.getMember("Z22K2").asString());
		assertFalse(result.hasMember("Z1K2"));
	}

	@Test
	public void testValueOfPairWithId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": {\"Z1K1\": \"Z22\", \"Z1K2\": \"Z0\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}}");
		assertEquals("first", result.getMember("Z22K1").asString());
		assertEquals("second", result.getMember("Z22K2").asString());
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
	public void testValueOfCharacterReferenceSpace() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z301\"}");
		assertEquals(" ", result.asString());
		assertNull(result.getMember("Z1K2"));
	}

	@Test
	public void testValueOfCharacterReferenceA() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z302\"}");
		assertEquals("a", result.asString());
		assertNull(result.getMember("Z1K2"));
	}

	@Test
	public void testValueOfCharacterReferenceSushi() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z303\"}");
		assertEquals("🍣", result.asString());
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
			assertZReference(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2"));
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
			assertZReference(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2"));
		}
	}

	@Test
	public void testTailOfSingleElementArray() {
		assertEquals("[]", eval("\"Z65\"").execute().execute((Object) new String[] { "A" }).toString());
	}

	@Test
	public void testTailOfTwoElementArray() {
		assertEquals("[B]", eval("\"Z65\"").execute().execute((Object) new String[] { "A", "B" }).toString());
	}

	@Test
	public void testTailOfThreeElementArray() {
		assertEquals("[B, C]", eval("\"Z65\"").execute().execute((Object) new String[] { "A", "B", "C" }).toString());
	}

	@Test
	public void testTailOfFourElementArray() {
		assertEquals("[B, C, D]",
				eval("\"Z65\"").execute().execute((Object) new String[] { "A", "B", "C", "D" }).toString());
	}

	@Test
	public void testTailOfEmptyArray() {
		try {
			eval("\"Z65\"").execute().execute((Object) new String[0]).toString();
			throw new IllegalStateException("Should have thrown an exception");
		} catch (PolyglotException e) {
			assertTrue(e.isGuestException());
			assertZReference(ZConstants.LISTISNIL, e.getGuestObject().getMember("Z1K2"));
		}
	}

	@Test
	public void testSameTrueTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z41\", \"K2\": \"Z41\"}").asBoolean());
	}

	@Test
	public void testSameTrueFalse() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z41\", \"K2\": \"Z42\"}").asBoolean());
	}

	@Test
	public void testSameTrueString() {
		assertEquals(false,
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z41\", \"K2\": \"true\"}").asBoolean());
	}

	@Test
	public void testSameTrueObjectTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": \"Z41\", \"K2\": "
				+ "{\"Z1K1\": \"Z40\", \"Z40K1\": \"Z41\"}}").asBoolean());
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
		return "{\"Z1K1\": \"Z8\", \"Z8K5\": \"Z61\", \"Z8K1\": [{\"Z17K2\": \"Z61K1\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": \"Z61\", \"Z14K4\": \"anonymous builtin of Z61\"}]}";
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
		if (first.getMember("Z22K1").asString().equals("Z1K2")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z22K1").asString());
		assertZReference("Z1", first.getMember("Z22K2"));
		assertEquals("Z1K2", second.getMember("Z22K1").asString());
		assertZReference("Z0", second.getMember("Z22K2"));
	}

	@Test
	public void testReifyCharacterWithoutId() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z22K1").asString().equals("Z60K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z22K1").asString());
		assertZReference("Z60", first.getMember("Z22K2"));
		assertEquals("Z60K1", second.getMember("Z22K1").asString());
		assertEquals("a", second.getMember("Z22K2").asString());
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
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z60", value.getMember("Z22K2"));
				break;
			case "Z1K2":
				assertFalse(sawId);
				sawId = true;
				assertZReference("Z0", value.getMember("Z22K2"));
				break;
			case "Z60K1":
				assertFalse(sawCharacter);
				sawCharacter = true;
				assertEquals("a", value.getMember("Z22K2").asString());
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
		if (first.getMember("Z22K1").asString().equals("Z6K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z22K1").asString());
		assertZReference("Z6", first.getMember("Z22K2"));
		assertEquals("Z6K1", second.getMember("Z22K1").asString());
		assertEquals("a string", second.getMember("Z22K2").asString());
	}

	@Test
	public void testReifyStringWithoutId() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"a string\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(2, result.getArraySize());
		Value first = result.getArrayElement(0);
		Value second = result.getArrayElement(1);
		if (first.getMember("Z22K1").asString().equals("Z6K1")) {
			Value swap = second;
			second = first;
			first = swap;
		}
		assertEquals("Z1K1", first.getMember("Z22K1").asString());
		assertZReference("Z6", first.getMember("Z22K2"));
		assertEquals("Z6K1", second.getMember("Z22K1").asString());
		assertEquals("a string", second.getMember("Z22K2").asString());
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
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z6", value.getMember("Z22K2"));
				break;
			case "Z1K2":
				assertFalse(sawId);
				sawId = true;
				assertZReference("Z0", value.getMember("Z22K2"));
				break;
			case "Z6K1":
				assertFalse(sawString);
				sawString = true;
				assertEquals("a string", value.getMember("Z22K2").asString());
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawId);
		assertTrue(sawString);
	}

	/**
	 * Assert that the result is a reified pair of a certain kind.
	 * 
	 * @param result The result of the call to Z37/reify
	 * @param first  Consumer that should run assertions on the first pair element
	 * @param second Consumer that should run assertions on the second pair element
	 */
	private void assertReifiedPair(Value result, Consumer<Value> first, Consumer<Value> second) {
		assertTrue(result.hasArrayElements());
		assertEquals(3, result.getArraySize());
		boolean sawType = false, sawFirst = false, sawSecond = false;
		for (int i = 0; i < 3; i++) {
			Value value = result.getArrayElement(i);
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z22", value.getMember("Z22K2"));
				break;
			case "Z22K1":
				assertFalse(sawFirst);
				sawFirst = true;
				first.accept(value.getMember("Z22K2"));
				break;
			case "Z22K2":
				assertFalse(sawSecond);
				sawSecond = true;
				second.accept(value.getMember("Z22K2"));
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawFirst);
		assertTrue(sawSecond);
	}

	@Test
	public void testReifyPairOfStrings() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z22\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}}");
		assertReifiedPair(result, (first) -> assertEquals("first", first.asString()),
				(second) -> assertEquals("second", second.asString()));
	}

	@Test
	public void testReifyPairOfReferences() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z22\", \"Z22K1\": \"Z22\", \"Z22K2\": \"Z10\"}}");
		assertReifiedPair(result, (first) -> assertZReference("Z22", first),
				(second) -> assertZReference("Z10", second));
	}

	@Test
	public void testReifyPairOfPairs() {
		String pairOfStrings = "{\"Z1K1\": \"Z22\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}";
		String pairOfReferences = "{\"Z1K1\": \"Z22\", \"Z22K1\": \"Z22\", \"Z22K2\": \"Z10\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z22\", \"Z22K1\": "
				+ pairOfStrings + ", \"Z22K2\": " + pairOfReferences + "}}");
		assertReifiedPair(result,
				(first) -> assertReifiedPair(first, (firstFirst) -> assertEquals("first", firstFirst.asString()),
						(firstSecond) -> assertEquals("second", firstSecond.asString())),
				(second) -> assertReifiedPair(second, (secondFirst) -> assertZReference("Z22", secondFirst),
						(secondSecond) -> assertZReference("Z10", secondSecond)));
	}

	private void assertReifiedList(Value result, boolean isNil, Consumer<Value> head, Consumer<Value> tail) {
		assertTrue(result.hasArrayElements());
		assertEquals(3, result.getArraySize());
		boolean sawType = false, sawHead = false, sawTail = false;
		for (int i = 0; i < 3; i++) {
			Value value = result.getArrayElement(i);
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z10", value.getMember("Z22K2"));
				break;
			case "Z10K1":
				assertFalse(sawHead);
				sawHead = true;
				head.accept(value.getMember("Z22K2"));
				break;
			case "Z10K2":
				assertFalse(sawTail);
				sawTail = true;
				tail.accept(value.getMember("Z22K2"));
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawHead);
		assertTrue(sawTail);
	}

	private void assertReifiedList(Value result, Consumer<Value> head, Consumer<Value> tail) {
		assertReifiedList(result, false, head, tail);
	}

	private void assertReifiedNil(Value result) {
		assertReifiedList(result, true, (head) -> assertZReference("Z441", head),
				(tail) -> assertZReference("Z441", tail));
	}

	@Test
	public void testReifyNil() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"Z13\"}");
		assertReifiedNil(result);
	}

	@Test
	public void testReifySingleElementList() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": [\"a\"]}");
		assertReifiedList(result, (head) -> assertEquals("a", head.asString()), (tail) -> assertReifiedNil(tail));
	}

	@Test
	public void testReifyTwoElementsList() {
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": [\"a\", \"b\"]}");
		assertReifiedList(result, (head) -> assertEquals("a", head.asString()), (tail) -> assertReifiedList(tail,
				(tailHead) -> assertEquals("b", tailHead.asString()), (tailTail) -> assertReifiedNil(tailTail)));
	}

	@Test
	public void testReifyPersistentObjectWithoutLabels() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z2\", \"Z2K1\": \"Z28\", \"Z2K2\": \"eneyj\"}}");
		assertTrue(result.hasArrayElements());
		assertEquals(3, result.getArraySize());
		boolean sawType = false, sawId = false, sawValue = false;
		for (int i = 0; i < 3; i++) {
			Value value = result.getArrayElement(i);
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z2", value.getMember("Z22K2"));
				break;
			case "Z2K1":
				assertFalse(sawId);
				sawId = true;
				assertEquals("Z28", value.getMember("Z22K2").asString());
				break;
			case "Z2K2":
				assertFalse(sawValue);
				sawValue = true;
				assertEquals("eneyj", value.getMember("Z22K2").asString());
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawId);
		assertTrue(sawValue);
	}

	@Test
	public void testReifyPersistentObjectWithLabels() {
		Value result = eval(
				"{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": {\"Z1K1\": \"Z2\", \"Z2K1\": \"Z28\", \"Z2K2\": \"eneyj\", "
						+ "\"Z2K3\": {\"Z1K1\": \"Z12\", \"Z12K1\": [{\"Z1K1\": \"Z11\", \"Z11K1\": \"en\", \"Z11K2\": \"project name\"}]}}}");
		assertTrue(result.hasArrayElements());
		assertEquals(4, result.getArraySize());
		boolean sawType = false, sawId = false, sawValue = false, sawLabels = false;
		for (int i = 0; i < 4; i++) {
			Value value = result.getArrayElement(i);
			Value labels = value.getMember("Z22K2");
			switch (value.getMember("Z22K1").asString()) {
			case "Z1K1":
				assertFalse(sawType);
				sawType = true;
				assertZReference("Z2", labels);
				break;
			case "Z2K1":
				assertFalse(sawId);
				sawId = true;
				assertEquals("Z28", labels.asString());
				break;
			case "Z2K2":
				assertFalse(sawValue);
				sawValue = true;
				assertEquals("eneyj", labels.asString());
				break;
			case "Z2K3":
				assertFalse(sawLabels);
				sawLabels = true;
				assertTrue(labels.hasArrayElements());
				assertEquals(2, labels.getArraySize());
				// no further assertions for now
				break;
			default:
				fail();
			}
		}
		assertTrue(sawType);
		assertTrue(sawId);
		assertTrue(sawValue);
		assertTrue(sawLabels);
	}

	@Test
	public void testAbstractObject() {
		String typePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z1\"}";
		String idPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K2\"}, \"Z22K2\": \"Z0\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": [" + typePair + ", " + idPair + "]}");
		assertTrue(result.hasMembers());
		assertEquals(2, result.getMemberKeys().size());
		assertZReference("Z1", result.getMember("Z1K1"));
		assertZReference("Z0", result.getMember("Z1K2"));
	}

	@Test
	public void testAbstractCharacter() {
		String typePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z60\"}";
		String characterPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z60K1\"}, \"Z22K2\": \"a\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": [" + typePair + ", " + characterPair + "]}");
		assertTrue(result.hasMembers());
		assertEquals(2, result.getMemberKeys().size());
		assertZReference("Z60", result.getMember("Z1K1"));
		assertEquals("a", result.getMember("Z60K1").asString());
		assertTrue(result.isString());
		assertEquals("a", result.asString());
	}

	@Test
	public void testAbstractCharacterWithId() {
		String typePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z60\"}";
		String idPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K2\"}, \"Z22K2\": \"Z0\"}";
		String characterPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z60K1\"}, \"Z22K2\": \"a\"}";
		String pairs = "[" + typePair + ", " + idPair + ", " + characterPair + "]";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + pairs + "}");
		assertTrue(result.hasMembers());
		assertEquals(3, result.getMemberKeys().size());
		assertZReference("Z60", result.getMember("Z1K1"));
		assertZReference("Z0", result.getMember("Z1K2"));
		assertEquals("a", result.getMember("Z60K1").asString());
		assertTrue(result.isString());
		assertEquals("a", result.asString());
	}

	@Test
	public void testAbstractString() {
		String typePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z6\"}";
		String stringPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z6K1\"}, \"Z22K2\": \"abc\"}";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": [" + typePair + ", " + stringPair + "]}");
		assertTrue(result.isString());
		assertEquals("abc", result.asString());
	}

	@Test
	public void testAbstractStringWithId() {
		String typePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z6\"}";
		String idPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K2\"}, \"Z22K2\": \"Z0\"}";
		String stringPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z6K1\"}, \"Z22K2\": \"abc\"}";
		String pairs = "[" + typePair + ", " + idPair + ", " + stringPair + "]";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + pairs + "}");
		assertTrue(result.hasMembers());
		assertEquals(3, result.getMemberKeys().size());
		assertZReference("Z6", result.getMember("Z1K1"));
		assertZReference("Z0", result.getMember("Z1K2"));
		assertEquals("abc", result.getMember("Z6K1").asString());
		assertTrue(result.isString());
		assertEquals("abc", result.asString());
	}

	@Test
	public void testAbstractProjectName() {
		// TODO migrate this to abstract a reified persistent object, moving the labels
		// outside the inner value – the problem is, then it’s not clear how to test the
		// labels, since currently a reified object delegates all interop messages to
		// the inner value, which no longer has the labels
		String typeTextPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z11\"}";
		String languageEnglishPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z11K1\"}, \"Z22K2\": \"Z251\"}";
		String textProjectNamePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z11K2\"}, \"Z22K2\": \"project_name\"}";
		String languageGermanPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z11K1\"}, \"Z22K2\": \"Z254\"}";
		String textProjektnamePair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z11K2\"}, \"Z22K2\": \"Projektname\"}";
		String englishLabelPairs = "[" + typeTextPair + ", " + languageEnglishPair + ", " + textProjectNamePair + "]";
		String germanLabelPairs = "[" + typeTextPair + ", " + languageGermanPair + ", " + textProjektnamePair + "]";
		String typeListPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z10\"}";
		String idNilPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K2\"}, \"Z22K2\": \"Z13\"}";
		String headListIsNilPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K1\"}, \"Z22K2\": \"Z441\"}";
		String tailListIsNilPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K2\"}, \"Z22K2\": \"Z441\"}";
		String nilPairs = "[" + typeListPair + ", " + idNilPair + ", " + headListIsNilPair + ", " + tailListIsNilPair
				+ "]";
		/*
		 * TODO Remove the nilPairs reassignment. Currently, there’s no way to get a
		 * correct idNilPair – "Z22K2": "Z13" will actually immediately evaluate to nil
		 * object, but we need it to be a ZReference with the ID Z13. There’s no way to
		 * create such a ZReference at the moment, so we cheat and instead of specifying
		 * the reified nil as JSON, we get it by actually calling reify(nil).
		 */
		nilPairs = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"Z13\"}";
		String headGermanLabelPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K1\"}, \"Z22K2\": "
				+ germanLabelPairs + "}";
		String tailNilPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K2\"}, \"Z22K2\": "
				+ nilPairs + "}";
		String headEnglishLabelPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K1\"}, \"Z22K2\": "
				+ englishLabelPairs + "}";
		String tailGermanLabelPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z10K2\"}, \"Z22K2\": "
				+ "[" + typeListPair + ", " + headGermanLabelPair + ", " + tailNilPair + "]" + "}";
		String labelsPairs = "[" + typeListPair + ", " + headEnglishLabelPair + ", " + tailGermanLabelPair + "]";
		String textsPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z12K1\"}, \"Z22K2\": "
				+ labelsPairs + "}";
		String typeMultilingualTextPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z12\"}";
		String labelPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K3\"}, \"Z22K2\": ["
				+ typeMultilingualTextPair + ", " + textsPair + "]}";
		String typeStringPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K1\"}, \"Z22K2\": \"Z6\"}";
		String idZ28Pair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z1K2\"}, \"Z22K2\": \"Z28\"}";
		String stringValueEneyjPair = "{\"Z1K1\": \"Z22\", \"Z22K1\": {\"Z1K1\": \"Z6\", \"Z6K1\": \"Z6K1\"}, \"Z22K2\": \"eneyj\"}";
		String object = "[" + typeStringPair + ", " + idZ28Pair + ", " + labelPair + ", " + stringValueEneyjPair + "]";
		Value result = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + object + "}");
		assertTrue(result.isString());
		assertEquals("eneyj", result.asString());
		assertTrue(result.hasMembers());
		assertEquals(4, result.getMemberKeys().size());
		assertZReference("Z6", result.getMember("Z1K1"));
		assertZReference("Z28", result.getMember("Z1K2"));
		assertEquals("eneyj", result.getMember("Z6K1").asString());
		Value labels = result.getMember("Z1K3");
		assertTrue(labels.hasMembers());
		assertEquals(2, labels.getMemberKeys().size());
		assertZReference("Z12", labels.getMember("Z1K1"));
		Value labelsList = labels.getMember("Z12K1");
		assertTrue(labelsList.hasArrayElements());
		assertEquals(2, labelsList.getArraySize());
		Value englishLabel = labelsList.getArrayElement(0);
		assertTrue(englishLabel.hasMembers());
		assertEquals(3, englishLabel.getMemberKeys().size());
		assertZReference("Z11", englishLabel.getMember("Z1K1"));
		assertZReference("Z251", englishLabel.getMember("Z11K1"));
		assertEquals("project_name", englishLabel.getMember("Z11K2").asString());
		Value germanLabel = labelsList.getArrayElement(1);
		assertTrue(germanLabel.hasMembers());
		assertEquals(3, germanLabel.getMemberKeys().size());
		assertZReference("Z11", germanLabel.getMember("Z1K1"));
		assertZReference("Z254", germanLabel.getMember("Z11K1"));
		assertEquals("Projektname", germanLabel.getMember("Z11K2").asString());
	}

	@Test
	public void testAbstractReifyString() {
		String reifyCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"a string\"}";
		assertEquals("a string", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + reifyCall + "}").asString());
	}

	@Test
	public void testAbstractReifyType() {
		String reifyCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"Z6\"}";
		Value stringType = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + reifyCall + "}");
		assertTrue(stringType.isMetaObject());
		assertEquals("Z6", stringType.getMetaSimpleName());
	}

	public void testSameAbstractReify(String value) {
		String reifyCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": " + value + "}";
		String abstractCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z38\", \"K1\": " + reifyCall + "}";
		String sameCall = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z33\", \"K1\": " + value + ", \"K2\": " + abstractCall + "}";
		assertEquals(true, eval(sameCall).asBoolean());
	}

	@Test
	public void testSameAbstractReifyObject() {
		testSameAbstractReify("{\"Z1K1\": \"Z1\", \"Z1K2\": \"Z0\"}");
	}

	@Test
	public void testSameAbstractReifyCharacterWithoutId() {
		testSameAbstractReify("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}");
	}

	@Test
	public void testSameAbstractReifyCharacterWithId() {
		testSameAbstractReify("{\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"a\"}");
	}

	@Test
	public void testSameAbstractReifyStringLiteral() {
		testSameAbstractReify("\"a string\"");
	}

	@Test
	public void testSameAbstractReifyStringWithoutId() {
		testSameAbstractReify("{\"Z1K1\": \"Z6\", \"Z6K1\": \"a string\"}");
	}

	@Test
	public void testSameAbstractReifyStringWithId() {
		testSameAbstractReify("{\"Z1K1\": \"Z6\", \"Z1K2\": \"Z0\", \"Z6K1\": \"a string\"}");
	}

	@Test
	public void testSameAbstractReifyPairOfStrings() {
		testSameAbstractReify("{\"Z1K1\": \"Z22\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}");
	}

	@Test
	public void testSameAbstractReifyPairOfReferences() {
		testSameAbstractReify("{\"Z1K1\": \"Z22\", \"Z22K1\": \"Z22\", \"Z22K2\": \"Z10\"}");
	}

	@Test
	public void testSameAbstractReifyPairOfPairs() {
		String pairOfStrings = "{\"Z1K1\": \"Z22\", \"Z22K1\": \"first\", \"Z22K2\": \"second\"}";
		String pairOfReferences = "{\"Z1K1\": \"Z22\", \"Z22K1\": \"Z22\", \"Z22K2\": \"Z10\"}";
		testSameAbstractReify(
				"{\"Z1K1\": \"Z22\", \"Z22K1\": " + pairOfStrings + ", \"Z22K2\": " + pairOfReferences + "}");
	}

	@Test
	public void testSameAbstractReifyNil() {
		testSameAbstractReify("\"Z13\"");
		testSameAbstractReify("[]");
	}

	@Test
	public void testSameAbstractReifySingleElementList() {
		testSameAbstractReify("[\"a\"]");
	}

	@Test
	public void testSameAbstractReifyTwoElementsList() {
		testSameAbstractReify("[\"a\", \"b\"]");
	}

	@Test
	public void testSameAbstractReifyProjectName() {
		testSameAbstractReify("\"Z28\"");
	}

}
