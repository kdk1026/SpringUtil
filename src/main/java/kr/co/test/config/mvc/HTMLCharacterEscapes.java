package kr.co.test.config.mvc;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 8. 김대광	최초작성
 * </pre>
 * 
 * <pre>
 * JSON 에 XSS 방지 처리
 * 설정 참고 - {@link kr.co.test.config.WebMvcConfig#jsonEscapeConverter}
 * </pre>
 * 
 * <pre>
 * @see StringEscapeUtils가 deprecated 되었으므로 commons-lang3가 아닌 commons-text를 dependency
 *   - commons-text에 commons-lang3가 의존성 걸려 있어서 가져온다.
 * </pre>  
 * @author 김대광
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

	private static final long serialVersionUID = 1L;

	private final int[] asciiEscapes;

	public HTMLCharacterEscapes() {
		asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
		asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
	}

	@Override
	public int[] getEscapeCodesForAscii() {
		return asciiEscapes;
	}

	@Override
	public SerializableString getEscapeSequence(int ch) {
		return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
	}

}
