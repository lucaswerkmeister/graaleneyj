package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class ZCharacterInteropTest extends ZTest {

	@Test
	public void testHasMembers() {
		assertTrue(eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}").hasMembers());
	}

	@Test
	public void testGetMembers() {
		Set<String> keys = eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}").getMemberKeys();
		assertEquals(2, keys.size());
		assertTrue(keys.contains("Z1K1"));
		assertTrue(keys.contains("Z60K1"));
	}

	@Test
	public void testReadMember_type() {
		assertEquals("Z60", eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}").getMember("Z1K1").toString());
	}

	@Test
	public void testReadMember_character() {
		assertEquals("a", eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}").getMember("Z60K1").asString());
	}

	@Test
	public void testReadMember_missing() {
		assertNull(eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}").getMember("Z1K2"));
	}

}
