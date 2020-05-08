package de.lucaswerkmeister.graaleneyj.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public final class ZMain {
	
	public static final String Z = "z"; // TODO name?
	
	public static void main(String[] args) throws IOException {
		Source source;
		switch (args.length) {
		case 0:
			source = Source.newBuilder(Z, new InputStreamReader(System.in), "<stdin>").build();
			break;
		case 1:
			source = Source.newBuilder(Z, new File(args[0])).build();
			break;
		default:
			System.err.println("Extra argument(s)!");
			System.exit(1);
			return; // unreachable but compiler doesn’t know that and complains about source being uninitialized
		}
		
		Context context = Context.newBuilder(Z).build();
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
