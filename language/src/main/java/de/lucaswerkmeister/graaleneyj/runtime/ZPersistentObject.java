package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.CachedContext;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZConstants;
import de.lucaswerkmeister.graaleneyj.ZLanguage;

@ExportLibrary(value = InteropLibrary.class, delegateTo = "value")
public class ZPersistentObject extends ZObject {

	private final String id;

	protected final Object value;

	private final Object labels;

	public ZPersistentObject(String id, Object value, Object labels) {
		super(STATIC_BLANK_SHAPE);
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
	public static class ToDisplayString {
		@Specialization(guards = { "receiver.getLabels() != null",
				"context.getUserLanguage().equals(cachedLanguage)" }, limit = "3")
		public static String doLabeledCached(ZPersistentObject receiver, boolean allowSideEffects,
				@CachedContext(ZLanguage.class) ZContext context,
				@CachedLibrary("receiver.getLabels()") InteropLibrary interop,
				@Cached("context.getUserLanguage()") String cachedLanguage,
				@Cached("doLabeled(receiver, allowSideEffects, context, interop)") String cachedResult) {
			return cachedResult;
		}

		@Specialization(guards = { "receiver.getLabels() != null" }, replaces = "doLabeledCached", limit = "3")
		public static String doLabeled(ZPersistentObject receiver, boolean allowSideEffects,
				@CachedContext(ZLanguage.class) ZContext context, @CachedLibrary(limit = "0") InteropLibrary interop) {
			String language = context.getUserLanguage();
			try {
				Object texts = interop.readMember(receiver.labels, ZConstants.MULTILINGUALTEXT_TEXTS);
				long length = interop.getArraySize(texts);
				String fallbackText = null;
				for (int i = 0; i < length; i++) {
					Object text = interop.readArrayElement(texts, i);
					String textLanguage = interop
							.asString(interop.readMember(text, ZConstants.MONOLINGUALTEXT_LANGUAGE));
					if (language.equals(textLanguage)) {
						return receiver.id + "/"
								+ interop.asString(interop.readMember(text, ZConstants.MONOLINGUALTEXT_TEXT));
					} else if ("en".equals(textLanguage)) { // TODO support more fallbacks than any→en
						fallbackText = interop.asString(interop.readMember(text, ZConstants.MONOLINGUALTEXT_TEXT));
					}
				}
				return receiver.id + "/" + fallbackText;
			} catch (UnsupportedMessageException | UnknownIdentifierException e) {
				return receiver.id;
			} catch (InvalidArrayIndexException e) {
				throw new IllegalStateException(e);
			}
		}

		@Specialization(guards = { "receiver.getLabels() == null" })
		public static String doUnlabeled(ZPersistentObject receiver, boolean allowSideEffects) {
			return receiver.id;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ZPersistentObject && id.equals(((ZPersistentObject) obj).id);
	}

}
