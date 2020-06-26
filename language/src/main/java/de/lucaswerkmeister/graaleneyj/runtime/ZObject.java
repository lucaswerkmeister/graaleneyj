package de.lucaswerkmeister.graaleneyj.runtime;

import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

@ExportLibrary(InteropLibrary.class)
public class ZObject implements TruffleObject {

	private final Map<String, Object> members;

	public ZObject(Map<String, Object> members) {
		this.members = Map.copyOf(members);
	}

	/**
	 * {@link ZObject} values are seem as objects with members by other languages.
	 */
	@ExportMessage
	public boolean hasMembers() {
		return true;
	}

	/**
	 * {@link ZObject} values are seen as objects with the IDs of their keys by
	 * other languages. That is, other languages always see keys like “Z10K1”, not
	 * “head”.
	 *
	 * @param booleanInternal Ignored, we have no internal keys.
	 */
	@ExportMessage
	public String[] getMembers(boolean includeInternal) {
		// TODO apparently the below call ends up calling methods that must not be part
		// of compilation
		return members.keySet().toArray(new String[members.size()]);
	}

	/**
	 * {@link ZObject} members are readable if a key of that ID exists.
	 */
	@ExportMessage
	public boolean isMemberReadable(String member) {
		return members.containsKey(member);
	}

	/**
	 * {@link ZObject} members are read by key ID.
	 */
	@ExportMessage
	public Object readMember(String member) {
		return members.get(member);
	}

	// no write-related methods are exported, objects are immutable

	@ExportMessage
	public final boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	public final Class<? extends TruffleLanguage<?>> getLanguage() {
		return ZLanguage.class;
	}

	@ExportMessage
	@TruffleBoundary
	public final String toDisplayString(boolean allowSideEffects, @CachedLibrary(limit = "0") InteropLibrary interops) {
		StringBuilder ret = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, Object> entry : members.entrySet()) {
			if (first) {
				first = false;
			} else {
				ret.append(", ");
			}
			ret.append('"').append(entry.getKey()).append("\": ");
			ret.append(interops.toDisplayString(entry.getValue(), allowSideEffects));
		}
		ret.append("}");
		return ret.toString();
	}

}
