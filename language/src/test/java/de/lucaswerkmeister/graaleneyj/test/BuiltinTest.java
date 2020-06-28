package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.graalvm.polyglot.PolyglotException;
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

}
