# GraalEneyj

A **work in progress** [Abstract Wikipedia][] / [Wikifunctions][] evaluation engine built on [GraalVM][].

It started out as an implementation of [eneyj][], an earlier version of the Wikifunctions model, hence the references to that name.

## Setup

Open a terminal and clone the repository, including Git submodules:

```sh
git clone --recurse-submodules https://github.com/lucaswerkmeister/graaleneyj.git
```

Ensure that the `JAVA_HOME` environment variable points to an installation of GraalVM 21.2.0, JDK11 version
(on Arch Linux, use the [jdk11-graalvm-bin][] package):

```sh
export JAVA_HOME=/path/to/java-11-graalvm/
# e.g. /usr/lib/jvm/java-11-graalvm/
```

Then, build GraalEneyj:

```sh
mvn package
```

## Current status

The `z` launcher script accepts a JSON value on standard input,
tries to evaluate it, and prints the result.

```sh
$ echo '"Hello, World!"' | ./z
Hello, World!
$ echo '{"Z1K1": "Z7", "Z7K1": "Z36", "K1": "Z28"}' | ./z
eneyj
```

Most of the “important” builtin functions are implemented,
and user-defined functions can be implemented in code (JavaScript or Python) or by calling other functions.
There is some support for reading objects from an installation of [WikiLambda][].
A migration from the [eneyj][] model to the [Wikifunctions function model][]
(specifically, the [pre-generic function model][] at first) is underway.
Once a test suite for other valuation engines is available ([T275093][]),
we’ll work towards compliance with it.

The `z` language can also be used together with other Graal languages,
by running them with `--vm.Dtruffle.class.path.append=` pointing to `graaleneyj.jar`.
For example, using [GraalPython][]:

```sh
$ graalpython --polyglot --jvm --vm.Dtruffle.class.path.append=language/target/graaleneyj.jar
Python 3.7.4 (Wed Feb 19 23:23:40 CET 2020)
[GraalVM CE, Java 11.0.6] on linux
Type "help", "copyright", "credits" or "license" for more information.
Please note: This Python implementation is in the very early stages, and can run little more than basic benchmarks at this point.
>>> import polyglot
>>> print(polyglot.eval(language='z', string='"Hello, World!"'))
Hello, World!
>>> polyglot.eval(language='z', string='"Z23"')
None
```

I have also briefly, but successfully, tested the same flags with the FastR, TruffleRuby and GraalJS (Node.js) launchers.

Since GraalVM 21.1.0 and continuing as of GraalVM 21.2.0,
implementations in Python are broken (see [oracle/graal#3372][]).

## High-level overview

[Truffle][] is an API for building language interpreters.
A parser creates an Abstract Syntax Tree (AST) consisting of tree nodes;
those tree nodes can then be evaluated into runtime values.

Broadly speaking, GraalEneyj is split into two kinds of classes:
classes representing tree nodes, and classes representing runtime values.
The parser ([`ZCanonicalJsonParser`][]) creates tree nodes from JSON input;
the tree nodes can then be evaluated into runtime values.
For example, a [`ZListLiteralNode`][] tree node is evaluated by evaluating its child nodes,
then collecting them into a [`ZList`][] runtime value
(a linked list ending with the `ZList.NIL` singleton).
Note that not all runtime values are instances of our custom classes:
for example, we map strings (`Z6`) and booleans (`Z41`, `Z42`) to the corresponding Java types.

GraalVM, then, is a special version of the Java Virtual Machine (JVM)
which knows how to compile language implementations written in Truffle into high-performance native code.
It can also compile and optimize code between language implementations:
given Truffle implementations of JavaScript and Python, for example,
it should be possible to call JavaScript and Python implementations of Z-functions
with little to no performance cost.

## IDE setup

Follow the [SimpleLanguage IDE setup instructions][]. Then:

1. Ensure that annotation processing is enabled. (In Eclipse, it’s in the project properties under Java Compiler > Annotation Processing.)
2. Add `truffle-dsl-processor.jar` as an annotation processor JAR. (In Eclipse, that’s in the project properties under Java Compiler > Annotation Processing > Factory Path. `truffle-dsl-processor.jar` should be in `$JAVA_HOME/lib/truffle/truffle-dsl-processor.jar`.)
3. Ensure that the project is not built using a GraalVM JRE, or else there’ll be duplicate errors because Truffle is built-in and also from Maven. (In Eclipse, that’s in the project properties under Java Build Path > Libraries > Modulepath > Java System Library.)

## Attribution

This is loosely based on [SimpleLanguage][], especially the `pom.xml` files.
SimpleLanguage is published under the UPL, which you may find in the `LICENSE.UPL` file in this source code repository.

## License

This project is published under the terms of the GNU General Public License,
either version 2 or (at your option) any later version.
A copy of the license may be found in the `LICENSE` file in this source code repository.
By sending a pull request or otherwise contributing to this project,
you agree to make your contribution available under this license.

[GraalVM]: https://www.graalvm.org/
[Truffle]: https://github.com/oracle/graal/tree/master/truffle#readme
[GraalPython]: https://github.com/graalvm/graalpython#readme
[`ZCanonicalJsonParser`]: language/src/main/java/de/lucaswerkmeister/graaleneyj/parser/ZCanonicalJsonParser.java
[`ZListLiteralNode`]: language/src/main/java/de/lucaswerkmeister/graaleneyj/nodes/ZListLiteralNode.java
[`ZList`]: language/src/main/java/de/lucaswerkmeister/graaleneyj/runtime/ZList.java
[eneyj]: https://github.com/google/abstracttext/tree/master/eneyj#readme
[SimpleLanguage IDE setup instructions]: https://www.graalvm.org/docs/graalvm-as-a-platform/implement-language/#ide-setup
[SimpleLanguage]: https://github.com/graalvm/simplelanguage#readme
[WikiLambda]: https://www.mediawiki.org/wiki/Special:MyLanguage/Extension:WikiLambda
[Wikifunctions function model]: https://meta.wikimedia.org/wiki/Special:MyLanguage/Abstract_Wikipedia/Function_model
[pre-generic function model]: https://meta.wikimedia.org/wiki/Special:MyLanguage/Abstract_Wikipedia/Pre-generic_function_model
[Abstract Wikipedia]: https://meta.wikimedia.org/wiki/Special:MyLanguage/Abstract_Wikipedia
[Wikifunctions]: https://meta.wikimedia.org/wiki/Special:MyLanguage/Wikifunctions
[T275093]: https://phabricator.wikimedia.org/T275093
[jdk11-graalvm-bin]: https://aur.archlinux.org/packages/jdk11-graalvm-bin/
[oracle/graal#3372]: https://github.com/oracle/graal/issues/3372
