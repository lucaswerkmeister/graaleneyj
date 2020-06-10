package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Before;
import org.junit.Test;

public class CodeJsTest extends ZTest {

	@Before
	public void checkJs() {
		assumeTrue("js language is installed", context.getEngine().getLanguages().containsKey("js"));
	}

	private String nandWithOnlyJsImplementation() {
		return "{\"Z1K1\": \"Z8\", \"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"javascript\", \"Z16K2\": \"K0 = !(Z53K1 && Z53K2)\"}}]}";
	}

	@Test
	public void testNandFalseFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyJsImplementation()
				+ ", \"K1\": \"Z55\", \"K2\": \"Z55\"}").asBoolean());
	}

	@Test
	public void testNandFalseTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyJsImplementation()
				+ ", \"K1\": \"Z55\", \"K2\": \"Z54\"}").asBoolean());
	}

	@Test
	public void testNandTrueFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyJsImplementation()
				+ ", \"K1\": \"Z54\", \"K2\": \"Z55\"}").asBoolean());
	}

	@Test
	public void testNandTrueTrue() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyJsImplementation()
				+ ", \"K1\": \"Z54\", \"K2\": \"Z54\"}").asBoolean());
	}

}
