package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;

import org.graalvm.polyglot.Value;
import org.junit.Test;

public class ZPairTest extends ZTest {

	@Test
	public void testPairAB() {
		Value pair = eval("\"Z355\"").execute();
		assertZReference("Z22", pair.getMember("Z1K1"));
		assertEquals("A", pair.getMember("Z22K1").asString());
		assertEquals("B", pair.getMember("Z22K2").asString());
	}

}
