package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BuiltinTest extends ZTest {

	@Test
	public void testValueOfProjectName() {
		assertEquals("eneyj", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z28\"}").asString());
	}

}
