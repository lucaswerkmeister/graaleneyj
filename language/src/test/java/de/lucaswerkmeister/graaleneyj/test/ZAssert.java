package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.graalvm.polyglot.Value;

public class ZAssert {

	private ZAssert() {
		// no instances
	}

	public static void assertZReference(String id, Value value) {
		assertEquals(id, value.toString());
		assertFalse("value should be a reference, not a string", value.isString());
		// TODO assert the “type” (meta object?) of value is ZReference
	}

}
