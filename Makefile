.PHONY: check-tck

ifndef JAVA_HOME
$(error JAVA_HOME must be set)
endif
ifndef GRAAL_HOME
$(error GRAAL_HOME must be set)
endif

language/target/graaleneyj.jar language/target/jars/truffle-api.jar language/target/jars/truffle-tck.jar:
	cd language && mvn package

tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class: tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.java language/target/jars/truffle-api.jar language/target/jars/truffle-tck.jar
	javac -cp language/target/jars/truffle-api.jar:language/target/jars/truffle-tck.jar $<

z-tck.jar: tck/src/de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class tck/src/META-INF/services/org.graalvm.polyglot.tck.LanguageProvider
	jar cfM $@ -C tck/src de/lucaswerkmeister/graaleneyj/tck/ZTCKLanguageProvider.class -C tck/src/ META-INF/services/org.graalvm.polyglot.tck.LanguageProvider

check-tck: z-tck.jar language/target/graaleneyj.jar
	python ~/git/graal/truffle/mx.truffle/tck.py -v -g "$(JAVA_HOME)" -cp $<:language/target/jars/truffle-tck.jar:"$(GRAAL_HOME)/truffle/mxbuild/dists/jdk1.8/truffle-tck-tests.jar":"$(GRAAL_HOME)/sdk/mxbuild/dists/jdk11/graal-sdk.jar" -lp language/target/graaleneyj.jar
