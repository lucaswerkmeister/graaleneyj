package de.lucaswerkmeister.graaleneyj.test;

import org.junit.Test;

public class PerfTest extends ZTest {

	@Test
	public void testPerf() {
		String code = "\"a value!\"";
		for (int i = 0; i < 1000; i++) {
			code = "{\"Z1K1\": \"Z100000\", \"Z100000K1\": " + code + "}";
		}
		for (int j = 0; j < 1000; j++) {
			eval(code);
		}
	}

}
