package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZLanguage;

@ExportLibrary(value = InteropLibrary.class, delegateTo = "value")
public class ZPersistentObject implements TruffleObject {

	private final String id;

	protected final Object value;

	private final Object labels;

	public ZPersistentObject(String id, Object value, Object labels) {
		this.id = id;
		this.value = value;
		this.labels = labels;
	}

	public String getId() {
		return id;
	}

	public Object getValue() {
		return value;
	}

	public Object getLabels() {
		return labels;
	}

	@ExportMessage
	public final boolean hasLanguage() {
		return true;
	}

	@ExportMessage
	public final Class<? extends TruffleLanguage<?>> getLanguage() {
		return ZLanguage.class;
	}

	@ExportMessage
	public final String toDisplayString(boolean allowSideEffects) {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ZPersistentObject && id.equals(((ZPersistentObject) obj).id);
	}

}
