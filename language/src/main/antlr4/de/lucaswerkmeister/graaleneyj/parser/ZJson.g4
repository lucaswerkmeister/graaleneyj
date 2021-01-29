grammar ZJson;

@parser::header {
	import de.lucaswerkmeister.graaleneyj.parser.JsonArray;
	import de.lucaswerkmeister.graaleneyj.parser.JsonElement;
	import de.lucaswerkmeister.graaleneyj.parser.JsonObject;
	import de.lucaswerkmeister.graaleneyj.parser.JsonString;
}

value returns [JsonElement element]:
	object { $element = $object.o; } |
	array { $element = $array.a; } |
	stringValue { $element = $stringValue.s; };
/* number/true/false/null not supported */

object returns [JsonObject o]:
	BEGIN_OBJECT {
		$o = new JsonObject();
		$o.setStartIndex($BEGIN_OBJECT.getStartIndex());
	}
	(
		firstKey=string NAME_SEPARATOR firstValue=value {
			$o.add($firstKey.s, $firstValue.element);
		}
		(
			VALUE_SEPARATOR
			otherKey=string NAME_SEPARATOR otherValue=value {
				$o.add($otherKey.s, $otherValue.element);
			}
		)*
	)?
	END_OBJECT {
		$o.setStopIndex($END_OBJECT.getStopIndex());
	};

array returns [JsonArray a]:
	BEGIN_ARRAY {
		$a = new JsonArray();
		$a.setStartIndex($BEGIN_ARRAY.getStartIndex());
	}
	(
		firstValue=value {
			$a.add($firstValue.element);
		}
		(
			VALUE_SEPARATOR
			otherValue=value {
				$a.add($otherValue.element);
			}
		)*
	)?
	END_ARRAY {
		$a.setStopIndex($END_ARRAY.getStopIndex());
	};

stringValue returns [JsonString s]: string {
	$s = new JsonString($string.s);
	$s.setStartIndex($string.startIndex);
	$s.setStopIndex($string.stopIndex);
};

string returns [String s, int startIndex, int stopIndex]: STRING {
	String content = $STRING.getText();
	int start = 1, end = content.length() - 1;
	StringBuilder sb = new StringBuilder();
	for (int i = start; i < end; i++) {
		int codepoint = content.codePointAt(i);
		if (codepoint == '\\') {
			int nextCodePoint = content.codePointAt(i+1);
			switch (nextCodePoint) {
			case '"':
				sb.append('"');
				break;
			case '\\':
				sb.append('\\');
				break;
			case '/':
				sb.append('/');
				break;
			case 'b':
				sb.append('\b');
				break;
			case 'f':
				sb.append('\f');
				break;
			case 'n':
				sb.append('\n');
				break;
			case 'r':
				sb.append('\r');
				break;
			case 't':
				sb.append('\t');
				break;
			default:
				throw new AssertionError("Illegal escape sequence!");
			}
			i++;
		}
		else {
			sb.appendCodePoint(codepoint);
			if (!Character.isBmpCodePoint(codepoint))
				i++;
		}
	}
	$s = sb.toString();
	$startIndex = $STRING.getStartIndex();
	$stopIndex = $STRING.getStopIndex();
};

BEGIN_OBJECT: '{';
END_OBJECT: '}';
BEGIN_ARRAY: '[';
END_ARRAY: ']';
NAME_SEPARATOR: ':';
VALUE_SEPARATOR: ',';
STRING: '"' CHAR* '"';
fragment CHAR: UNESCAPED | ESCAPED;
fragment UNESCAPED: [\u0020-\u0021\u0023-\u005B\u005D-\u{10FFFF}];
fragment ESCAPED: '\\' ["\\/bfnrt] | '\\u' HEX HEX HEX HEX;
fragment HEX: [0-9a-fA-F];
WS: [\t\n\r ]+ -> skip;
