.PHONY: check-tck

language/target/graaleneyj.jar language/target/jars/truffle-api.jar language/target/jars/truffle-tck.jar:
	cd language && mvn package

tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class: tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.java language/target/jars/truffle-api.jar language/target/jars/truffle-tck.jar
	javac -cp language/target/jars/truffle-api.jar:language/target/jars/truffle-tck.jar $<

z-tck.jar: tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class tck/src/META-INF/services/org.graalvm.polyglot.tck.LanguageProvider
	jar cfM $@ -C tck/src de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class -C tck/src/ META-INF/services/org.graalvm.polyglot.tck.LanguageProvider

check-tck: z-tck.jar language/target/graaleneyj.jar
	python ~/git/graal/truffle/mx.truffle/tck.py -g "$(JAVA_HOME)" -cp $< -lp language/target/graaleneyj.jar
