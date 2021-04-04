package de.lucaswerkmeister.graaleneyj.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graalvm.polyglot.io.FileSystem;

/**
 * A {@link FileSystem} which reads ZObjects from a wiki with the WikiLambda
 * extension installed. Only supports paths of the form
 * abstracttext/eneyj/data/Z123.json, and only supports reading.
 */
public class WikiLambdaFileSystem implements FileSystem {

	private static final Pattern PATH_PATTERN = Pattern.compile("^abstracttext/eneyj/data/(Z[1-9][0-9]*).json$");

	private final String protocol;
	private final String host;
	private final String scriptPath;

	/**
	 * Return the path which this file system expects to see for an object ID. This
	 * path also matches the path of objects in the real file system.
	 */
	public static Path zidToPath(String zid) {
		return Path.of("abstracttext", "eneyj", "data", zid + ".json");
	}

	public WikiLambdaFileSystem(String host) {
		this("https", host, "/w");
	}

	public WikiLambdaFileSystem(String protocol, String host, String scriptPath) {
		this.protocol = protocol;
		this.host = host;
		this.scriptPath = scriptPath;
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
		if (modes.contains(AccessMode.WRITE)) {
			throw new IOException("Read-only file system");
		}
		if (!PATH_PATTERN.matcher(path.toString()).matches()) {
			throw new IOException("Path does not match expected pattern");
		}
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		throw new IOException("Read-only file system");
	}

	@Override
	public void delete(Path path) throws IOException {
		throw new IOException("Read-only file system");
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		Matcher matcher = PATH_PATTERN.matcher(path.toString());
		if (!matcher.matches()) {
			throw new IOException("Path does not match expected pattern");
		}
		String zid = matcher.group(1);
		URL url = new URL(protocol, host,
				scriptPath + "/index.php?action=raw&ctype=application/json&title=ZObject:" + zid);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream inputStream = connection.getInputStream();
		if (connection.getResponseCode() != 200) {
			throw new IOException("Bad response for " + zid + ": " + connection.getResponseCode());
		}
		byte[] bytes = inputStream.readAllBytes();
		if (bytes.length == 0) {
			throw new IOException("Empty response for " + zid);
		}
		return new ByteArraySeekableByteChannel(bytes);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
		throw new IOException("Not a directory");
	}

	@Override
	public Path toAbsolutePath(Path path) {
		return path;
	}

	@Override
	public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
		return path;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		return Map.of();
	}

}
