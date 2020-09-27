package de.lucaswerkmeister.graaleneyj.test;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Set;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import de.lucaswerkmeister.graaleneyj.filesystem.UnionFileSystem;
import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public class WikiLambdaTest {

	@Test
	public void testWikiLambda() {
		WikiLambdaFileSystem wikiLambdaFileSystem = new WikiLambdaFileSystem("notwikilambda.toolforge.org");
		Path z28Path = WikiLambdaFileSystem.zidToPath("Z28");
		UnionFileSystem fileSystem = new UnionFileSystem(Set.of(z28Path), wikiLambdaFileSystem);
		Context context = Context.newBuilder() //
				.allowIO(true) // needed for .fileSystem()
				.fileSystem(fileSystem) //
				.build();
		Value project_name = context.eval("z", "{\"Z1K1\": \"Z7\", \"Z7K1\": \"Z36\", \"K1\": \"Z28\"}");
		assertEquals("eneyj", project_name.asString());
	}

}
