package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;

public class PrimitiveTest {

	private Context context;

	@Before
	public void setUp() {
		context = Context.create("z");
	}

	@After
	public void tearDown() {
		context = null;
	}

	private Value eval(String code) {
		Source source = Source.newBuilder(ZLanguage.ID, code, "test").buildLiteral();
		return context.eval(source);
	}

	@Test
	public void testNil() {
		assertTrue(eval("\"" + ZConstants.NIL + "\"").isNull());
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
