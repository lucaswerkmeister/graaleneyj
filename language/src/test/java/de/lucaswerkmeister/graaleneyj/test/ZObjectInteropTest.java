package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class ZObjectInteropTest extends ZTest {

	@Test
	public void testHasMembers() {
		assertTrue(eval("{\"Z1K1\": \"Z1\"}").hasMembers());
	}

	@Test
	public void testGetMembers() {
		Set<String> keys = eval("{\"Z1K1\": \"Z1\"}").getMemberKeys();
		assertEquals(1, keys.size());
		assertTrue(keys.contains("Z1K1"));
	}

	@Test
	public void testReadMember() {
		assertEquals("Z1", eval("{\"Z1K1\": \"Z1\"}").getMember("Z1K1").toString());
	}

	@Test
	public void testReadMember_missing() {
		assertNull(eval("{\"Z1K1\": \"Z1\"}").getMember("Z1K2"));
	}

}
