package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

	@Test
	public void testEmptyString() {
		Source source = Source.newBuilder(ZLanguage.ID, "\"\"", "test").buildLiteral();
		Value value = context.eval(source);
		assertEquals("", value.asString());
	}

	@Test
	public void testNonemptyString() {
		Source source = Source.newBuilder(ZLanguage.ID, "\"Hello, World!\"", "test").buildLiteral();
		Value value = context.eval(source);
		assertEquals("Hello, World!", value.asString());
	}

}
