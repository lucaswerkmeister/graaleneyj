package de.lucaswerkmeister.graaleneyj.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.FileSystem;

import de.lucaswerkmeister.graaleneyj.filesystem.WikiLambdaFileSystem;

public final class ZMain {

	public static final String Z = "z"; // TODO name?

	public static void main(String[] args) throws IOException {
		Source.Builder sourceBuilder = Source.newBuilder(Z, new InputStreamReader(System.in), "<stdin>");
		Context.Builder contextBuilder = Context.newBuilder().allowPolyglotAccess(PolyglotAccess.ALL);
		int positionalArguments = 0;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("-")) {
				if (arg.startsWith("--wiki-host=")) {
					String host = arg.substring("--wiki-host=".length());
					FileSystem fs = new WikiLambdaFileSystem(host);
					contextBuilder.allowIO(true); // required for custom file system
					contextBuilder.fileSystem(fs);
				} else if (arg.equals("--wiki-host")) {
					if (!(++i < args.length)) {
						System.err.println("Missing argument after --wiki-host!");
						System.exit(1);
						return;
					}
					String host = args[i];
					FileSystem fs = new WikiLambdaFileSystem(host);
					contextBuilder.allowIO(true); // required for custom file system
					contextBuilder.fileSystem(fs);
				} else if (arg.equals("-")) {
					sourceBuilder = Source.newBuilder(Z, new InputStreamReader(System.in), "-");
				} else {
					System.err.println("Unknown option: " + arg);
					System.exit(1);
					return;
				}
			} else {
				if (++positionalArguments > 1) {
					System.err.println("Extra argument(s)!");
					System.exit(1);
					return;
				}
				sourceBuilder = Source.newBuilder(Z, new File(arg));
			}
		}

		Source source = sourceBuilder.build();
		Context context = contextBuilder.build();
		try {
			Value result = context.eval(source);
			if (!result.isNull()) {
				System.out.println(result);
			}
		} catch (PolyglotException e) {
			e.printStackTrace();
		} finally {
			context.close();
		}
	}

}
