package de.lucaswerkmeister.graaleneyj.library;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import de.lucaswerkmeister.graaleneyj.ZConstants;

final class ZTypeIdentityDefaults {

	@ExportLibrary(value = ZTypeIdentityLibrary.class, receiverType = Boolean.class)
	static final class DefaultBooleanExports {
		@ExportMessage
		public static boolean hasTypeIdentity(Boolean receiver) {
			return true;
		}

		@ExportMessage
		public static String getTypeIdentity(Boolean receiver) {
			return ZConstants.BOOLEAN;
		}
	}

	@ExportLibrary(value = ZTypeIdentityLibrary.class, receiverType = Integer.class)
	static final class DefaultIntegerExports {
		@ExportMessage
		public static boolean hasTypeIdentity(Integer receiver) {
			return true;
		}

		@ExportMessage
		public static String getTypeIdentity(Integer receiver) {
			return ZConstants.CHARACTER;
		}
	}

	@ExportLibrary(value = ZTypeIdentityLibrary.class, receiverType = String.class)
	static final class DefaultStringExports {
		@ExportMessage
		public static boolean hasTypeIdentity(String receiver) {
			return true;
		}

		@ExportMessage
		public static String getTypeIdentity(String receiver) {
			return ZConstants.STRING;
		}
	}

}
