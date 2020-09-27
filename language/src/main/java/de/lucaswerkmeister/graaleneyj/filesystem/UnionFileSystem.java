package de.lucaswerkmeister.graaleneyj.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.io.FileSystem;

/**
 * A file system that delegates between two other file systems. A fixed set of
 * special paths delegates to a special file system, all others delegate to a
 * default file system. (That default file system may in turn be another
 * {@link UnionFileSystem}, chaining multiple sets of special paths this way.)
 */
public class UnionFileSystem implements FileSystem {

	private final Set<Path> specialPaths;
	private final FileSystem specialFileSystem;
	private final FileSystem defaultFileSystem;

	public UnionFileSystem(Set<Path> specialPaths, FileSystem specialFileSystem) {
		this(specialPaths, specialFileSystem, FileSystem.newDefaultFileSystem());
	}

	public UnionFileSystem(Set<Path> specialPaths, FileSystem specialFileSystem, FileSystem defaultFileSystem) {
		this.specialPaths = specialPaths;
		this.specialFileSystem = specialFileSystem;
		this.defaultFileSystem = defaultFileSystem;
	}

	private FileSystem selectFileSystem(Path path) {
		if (specialPaths.contains(path)) {
			return specialFileSystem;
		} else {
			return defaultFileSystem;
		}
	}

	@Override
	public Path parsePath(URI uri) {
		return Path.of(uri);
	}

	@Override
	public Path parsePath(String path) {
		return Path.of(path);
	}

	@Override
	public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
		selectFileSystem(path).checkAccess(path, modes, linkOptions);
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		selectFileSystem(dir).createDirectory(dir, attrs);
	}

	@Override
	public void delete(Path path) throws IOException {
		selectFileSystem(path).delete(path);
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		return selectFileSystem(path).newByteChannel(path, options, attrs);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
		return selectFileSystem(dir).newDirectoryStream(dir, filter);
	}

	@Override
	public Path toAbsolutePath(Path path) {
		return selectFileSystem(path).toAbsolutePath(path);
	}

	@Override
	public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
		return selectFileSystem(path).toRealPath(path, linkOptions);
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		return selectFileSystem(path).readAttributes(path, attributes, options);
	}

}
