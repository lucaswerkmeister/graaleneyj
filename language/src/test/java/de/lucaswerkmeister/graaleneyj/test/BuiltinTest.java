package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.Test;

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
			// TODO test e.getGuestObject()
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
			eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z66\", \"K1\": []}");
			throw new IllegalStateException("Should have thrown an exception");
		} catch (PolyglotException e) {
			assertTrue(e.isGuestException());
			// TODO test e.getGuestObject()
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
			// TODO test e.getGuestObject()
		}
	}

}
