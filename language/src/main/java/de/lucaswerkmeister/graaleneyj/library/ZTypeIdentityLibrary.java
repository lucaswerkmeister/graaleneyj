package de.lucaswerkmeister.graaleneyj.library;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.GenerateLibrary.Abstract;
import com.oracle.truffle.api.library.GenerateLibrary.DefaultExport;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;

import de.lucaswerkmeister.graaleneyj.runtime.ZObject;
import de.lucaswerkmeister.graaleneyj.runtime.ZType;

/**
 * Internal library for getting the {@link ZType#identity identity} of the type
 * of an object. (Usually, that object is a {@link ZObject}, but the library is
 * also exported for some other types that we use as unboxed values.)
 */
@GenerateLibrary
@DefaultExport(ZTypeIdentityDefaults.DefaultBooleanExports.class)
@DefaultExport(ZTypeIdentityDefaults.DefaultIntegerExports.class)
@DefaultExport(ZTypeIdentityDefaults.DefaultStringExports.class)
public abstract class ZTypeIdentityLibrary extends Library {

	/**
	 * Whether the receiver implements this library.
	 */
	@Abstract(ifExported = { "getTypeIdentity" })
	public boolean hasTypeIdentity(Object receiver) {
		return false;
	}

	/**
	 * Get the identity of the type of the receiver (a Z-ID).
	 */
	@Abstract(ifExported = { "hasTypeIdentity" })
	public String getTypeIdentity(Object receiver) {
		throw new UnsupportedOperationException();
	}

	private static final LibraryFactory<ZTypeIdentityLibrary> FACTORY = LibraryFactory
			.resolve(ZTypeIdentityLibrary.class);
	private static final ZTypeIdentityLibrary UNCACHED = FACTORY.getUncached();

	public static LibraryFactory<ZTypeIdentityLibrary> getFactory() {
		return FACTORY;
	}

	public static ZTypeIdentityLibrary getUncached() {
		return UNCACHED;
	}

	public static ZTypeIdentityLibrary getUncached(Object receiver) {
		return FACTORY.getUncached(receiver);
	}

}
