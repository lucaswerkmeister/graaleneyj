package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ControlFlowTest extends ZTest {

	@Test
	public void testIfTrueConstant() {
		assertEquals("then",
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z31\", \"K1\": \"Z54\", \"K2\": \"then\", \"K3\": \"else\"}")
						.asString());
	}

	@Test
	public void testIfElseConstant() {
		assertEquals("else",
				eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z31\", \"K1\": \"Z55\", \"K2\": \"then\", \"K3\": \"else\"}")
						.asString());
	}

	@Test
	public void testIfNamedArguments() {
		assertEquals("then", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z31\", "
				+ "\"Z31K1\": \"Z54\", \"Z31K2\": \"then\", \"Z31K3\": \"else\"}").asString());
	}

	@Test
	public void testIfValueOfTrue() {
		String condition = "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z54\"}";
		assertEquals("then", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z31\", " + "\"K1\": " + condition
				+ ", \"K2\": \"then\", \"K3\": \"else\"}").asString());
	}

}