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
[eneyj]: https://github.com/google/abstracttext/tree/master/eneyj#readme
[SimpleLanguage]: https://github.com/graalvm/simplelanguage#readme
