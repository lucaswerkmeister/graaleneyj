package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.ZConstants;

public class PrimitiveTest extends ZTest {

	@Test
	public void testNothing() {
		assertTrue(eval("\"" + ZConstants.NOTHING + "\"").isNull());
	}

	@Test
	public void testTrue() {
		assertEquals(true, eval("\"" + ZConstants.TRUE + "\"").asBoolean());
	}

	@Test
	public void testFalse() {
		assertEquals(false, eval("\"" + ZConstants.FALSE + "\"").asBoolean());
	}

	@Test
	public void testEmptyString() {
		assertEquals("", eval("\"\"").asString());
	}

	@Test
	public void testNonemptyString() {
		assertEquals("Hello, World!", eval("\"Hello, World!\"").asString());
	}

}
