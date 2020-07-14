package de.lucaswerkmeister.graaleneyj.tck;

import java.util.Collection;
import java.util.Collections;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.tck.LanguageProvider;
import org.graalvm.polyglot.tck.Snippet;

public class ZTCKLanguageProvider implements LanguageProvider {

    private static final String ID = "z";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Value createIdentityFunction(Context context) {
        return context.eval(ID, "{\"Z1K1\": \"Z8\", \"Z8K1\": [{\"Z1K2\": \"K1\"}], \"Z8K4\": [{\"Z1K1\": \"Z14\", \"Z14K1\": {\"Z1K1\": \"Z16\", \"Z16K1\": \"javascript\", \"Z16K2\": \"K0=K1\"}}]}");
    }

    @Override
    public Collection<? extends Snippet> createValueConstructors(Context context) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Snippet> createExpressions(Context context) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Snippet> createStatements(Context context) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Snippet> createScripts(Context context) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Source> createInvalidSyntaxScripts(Context context) {
        return Collections.emptyList();
    }

}
