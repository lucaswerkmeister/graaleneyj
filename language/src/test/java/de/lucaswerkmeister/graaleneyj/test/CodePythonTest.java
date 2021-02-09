package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Before;
import org.junit.Test;

public class CodePythonTest extends ZTest {

	@Before
	public void checkPython() {
		assumeTrue("python language is installed", context.getEngine().getLanguages().containsKey("python"));
	}

	private String nandWithOnlyPythonImplementation() {
		return "{\"Z1K1\": \"Z8\", \"Z1K2\": \"Z53\", \"Z8K1\": [{\"Z17K2\": \"Z53K1\"}, {\"Z17K2\": \"Z53K2\"}], "
				+ "\"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"python\", \"Z16K2\": \"K0 = not(Z53K1 and Z53K2)\"}}]}";
	}

	@Test
	public void testNandFalseFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z55\", \"K2\": \"Z55\"}").asBoolean());
	}

	@Test
	public void testNandFalseTrue() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z55\", \"K2\": \"Z54\"}").asBoolean());
	}

	@Test
	public void testNandTrueFalse() {
		assertEquals(true, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z54\", \"K2\": \"Z55\"}").asBoolean());
	}

	@Test
	public void testNandTrueTrue() {
		assertEquals(false, eval("{\"Z1K1\": \"Z7\", \"Z7K1\": " + nandWithOnlyPythonImplementation()
				+ ", \"K1\": \"Z54\", \"K2\": \"Z54\"}").asBoolean());
	}

}
