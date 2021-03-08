package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;
import org.junit.Test;

public class ZTypeInteropTest extends ZTest {

	@Test
	public void testKeys() {
		Value stringType = eval("\"Z6\"").execute();
		assertTrue(stringType.hasMembers());
		assertEquals(Set.of("Z1K1", "Z4K1", "Z4K2", "Z4K4"), stringType.getMemberKeys());
		assertZReference("Z4", stringType.getMember("Z1K1"));
		assertZReference("Z6", stringType.getMember("Z4K1"));
		assertTrue(stringType.getMember("Z4K2").hasMembers());
		assertZReference("Z582", stringType.getMember("Z4K4"));
	}

	@Test
	public void testNames() {
		Value stringType = eval("\"Z6\"").execute();
		assertEquals("Z6", stringType.getMetaSimpleName());
		assertEquals("Z6", stringType.getMetaQualifiedName());
	}

	@Test
	public void testIsMetaInstance() {
		Value typeType = eval("\"Z4\"").execute();
		Value stringType = eval("\"Z6\"").execute();
		Value functionType = eval("\"Z8\"").execute();
		Value referenceType = eval("\"Z9\"").execute();
		Value listType = eval("\"Z10\"").execute();
		Value booleanType = eval("\"Z50\"").execute();
		Value characterType = eval("\"Z60\"").execute();

		Value nothing = eval("\"Z23\"");
		Value unboxedString = eval("\"string\"");
		Value boxedString = eval("{\"Z1K1\": \"Z6\", \"Z6K1\": \"string\", \"Z1K2\": \"Z0\"}");
		Value function = eval("\"Z36\"").execute();
		Value projectNameReference = eval("\"Z28\"");
		Value emptyList = eval("[]");
		Value nonemptyList = eval("[\"element\"]");
		Value trueValue = eval("\"Z54\"");
		Value falseValue = eval("\"Z55\"");
		Value unboxedCharacter = eval("{\"Z1K1\": \"Z60\", \"Z60K1\": \"a\"}");
		Value boxedCharacter = eval("{\"Z1K1\": \"Z60\", \"Z1K2\": \"Z0\", \"Z60K1\": \"a\"}");

		Map<Value, List<Value>> typesWithInstances = Map.of( //
				typeType, List.of(typeType, stringType, nothing), //
				stringType, List.of(unboxedString, boxedString), //
				functionType, List.of(function), //
				referenceType, List.of(projectNameReference), //
				listType, List.of(emptyList, nonemptyList), //
				booleanType, List.of(trueValue, falseValue), //
				characterType, List.of(unboxedCharacter, boxedCharacter) //
		);
		for (Value type : typesWithInstances.keySet()) {
			for (Value instance : typesWithInstances.get(type)) {
				assertTrue(instance + " must be an instance of " + type, type.isMetaInstance(instance));
				if (instance == unboxedString || instance == trueValue || instance == falseValue) {
					continue;
				}
				assertEquals(type, instance.getMetaObject());
			}
			for (Value otherType : typesWithInstances.keySet()) {
				if (type == otherType) {
					continue;
				}
				for (Value otherInstance : typesWithInstances.get(otherType)) {
					assertFalse(otherInstance + " must not be an instance of " + type,
							type.isMetaInstance(otherInstance));
				}
			}
		}
	}

	@Test
	public void testToDisplayString() {
		Value reifiedStringTypePersistentObject = eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z37\", \"K1\": \"Z6\"}");
		for (long l = 0; l < reifiedStringTypePersistentObject.getArraySize(); l++) {
			Value pair = reifiedStringTypePersistentObject.getArrayElement(l);
			if (pair.getMember("Z22K1").asString().equals("Z2K2")) {
				Value reifiedStringType = pair.getMember("Z22K2");
				Value stringType = eval("\"Z38\"").execute().execute(reifiedStringType);
				assertEquals("Z6", stringType.toString());
				return;
			}
		}
		fail("Did not find persistent object value pair in reified string type persistent object");
	}

}
