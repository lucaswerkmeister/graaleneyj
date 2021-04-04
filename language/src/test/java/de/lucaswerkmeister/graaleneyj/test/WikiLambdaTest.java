package de.lucaswerkmeister.graaleneyj.test;

import static de.lucaswerkmeister.graaleneyj.test.ZAssert.assertZReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.ZLanguage;
import de.lucaswerkmeister.graaleneyj.filesystem.UnionFileSystem;
import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public class WikiLambdaTest {

	// TODO unify parts of setup/teardown/eval with ZTest?

	private Context context;

	@Before
	public void setUp() {
		WikiLambdaFileSystem wikiLambdaFileSystem = new WikiLambdaFileSystem("notwikilambda.toolforge.org");
		Path z1Path = WikiLambdaFileSystem.zidToPath("Z1");
		Path z28Path = WikiLambdaFileSystem.zidToPath("Z28");
		Path z777Path = WikiLambdaFileSystem.zidToPath("Z777");
		UnionFileSystem fileSystem = new UnionFileSystem(Set.of(z1Path, z28Path, z777Path), wikiLambdaFileSystem);
		context = Context.newBuilder() //
				.allowIO(true) // needed for .fileSystem()
				.fileSystem(fileSystem) //
				.build();
	}

	@After
	public void tearDown() {
		context = null;
	}

	private Value eval(String code) {
		Source source = Source.newBuilder(ZLanguage.ID, code, "test").buildLiteral();
		return context.eval(source);
	}

	@Test
	public void testValueOfProjectName() {
		assertEquals("eneyj", eval("{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z28\"}").asString());
	}

	@Test
	public void testZTypeMembers() {
		Value ztype = eval("\"Z1\"").execute();
		assertTrue(ztype.hasMembers());
		Value identity = ztype.getMember("Z4K1");
		assertZReference("Z1", identity);
		Value keys = ztype.getMember("Z4K2");
		assertTrue(keys.hasArrayElements());
		assertEquals(1, keys.getArraySize());
		Value typeKey = keys.getArrayElement(0);
		assertZReference("Z3", typeKey.getMember("Z1K1"));
		// no further assertions on the typeKey for now
		Value validator = ztype.getMember("Z4K3");
		assertZReference("Z30", validator);
		// TODO all of the above tests the inner object of the persistent object;
		// test the label of the outer object somehow?
	}

	@Test
	public void testPersistentFunctionCall() {
		Value result = eval("\"Z777\"").execute();
		assertTrue(result.isBoolean());
		assertFalse(result.asBoolean());
	}

}
