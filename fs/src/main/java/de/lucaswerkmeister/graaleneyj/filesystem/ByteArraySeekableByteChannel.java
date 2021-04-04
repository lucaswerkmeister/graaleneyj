package de.lucaswerkmeister.graaleneyj.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

/**
 * A {@link SeekableByteChannel} that wraps a byte array.
 */
public class ByteArraySeekableByteChannel implements SeekableByteChannel {

	private final byte[] bytes;
	private int position;
	private boolean open = true;

	/**
	 * Create a new seekable byte channel for the given byte array. The input array
	 * is <em>not</em> copied.
	 */
	public ByteArraySeekableByteChannel(byte[] bytes) {
		this.bytes = bytes;
		this.position = 0;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() throws IOException {
		open = false;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		if (!open) {
			throw new ClosedChannelException();
		}
		int length = Math.min(bytes.length - position, dst.remaining());
		dst.put(bytes, position, length);
		position += length;
		return length;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		throw new NonWritableChannelException();
	}

	@Override
	public long position() throws IOException {
		return position;
	}

	@Override
	public SeekableByteChannel position(long newPosition) throws IOException {
		if (!open) {
			throw new ClosedChannelException();
		}
		if (newPosition < 0 || newPosition > bytes.length) {
			throw new IllegalArgumentException("Bad new position: " + newPosition);
		}
		position = (int) newPosition;
		return this;
	}

	@Override
	public long size() throws IOException {
		return bytes.length;
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		throw new NonWritableChannelException();
	}

}
