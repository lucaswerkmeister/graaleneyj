# GraalEneyj

**EXTREMELY** early work in progress.
Hoping to maybe make a [GraalVM][] implementation of [eneyj][].

## What’s ready so far

The `z` launcher script accepts a JSON value on standard input,
tries to evaluate it, and prints the result.

```sh
$ export JAVA_HOME=/path/to/java-11-graalvm/
$ mvn package
$ echo '"Hello, World!"' | ./z
Hello, World!
$ echo '["Hello, World!"]' | ./z
de.lucaswerkmeister.graaleneyj.runtime.ZList@548a24a
```

Evaluating JSON *objects* doesn’t really work yet.
(Also, the path to the eneyj data – to resolve e. g. a reference to `Z1` – is currently hard-coded.)

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
>>> polyglot.eval(string='Z13', language='z')
None
```

I have also briefly, but successfully, tested the same flags with the FastR, TruffleRuby and GraalJS (Node.js) launchers.

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
for example, we map strings (`Z6`) and booleans (`Z54`, `Z55`) to the corresponding Java types.

GraalVM, then, is a special version of the Java Virtual Machine (JVM)
which knows how to compile language implementations written in Truffle into high-performance native code.
It can also compile and optimize code between language implementations:
given Truffle implementations of JavaScript and Python, for example,
it should be possible to call JavaScript and Python implementations of Z-functions
with little to no performance cost.

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
[SimpleLanguage]: https://github.com/graalvm/simplelanguage#readme
