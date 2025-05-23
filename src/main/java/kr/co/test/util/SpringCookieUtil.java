package kr.co.test.util;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 7. 3.  kdk	 	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * </pre>
 *
 * <pre>
 * Spring 5.0 부터 지원
 *   - 5.1 부터는 사용 빈도는 적으나 sameSite 설정 가능
 *   - <a href="https://cherish-it.tistory.com/12">sameSite 참고</a>
 * </pre>
 * @author kdk
 */
public class SpringCookieUtil {

	private static SpringCookieUtil instance;

	private SpringCookieUtil() {
		super();
	}

	public static synchronized SpringCookieUtil getInstance() {
		if (instance == null) {
			instance = new SpringCookieUtil();
		}

		return instance;
	}

	/**
	 * Spring 쿠키 설정
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param isSecure
	 * @param isHttpOnly
	 * @param domain
	 */
	public void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isSecure, boolean isHttpOnly, String domain) {
		if ( response == null ) {
			throw new IllegalArgumentException("response is null");
		}

		if ( StringUtils.hasText(name) ) {
			throw new IllegalArgumentException("name is null");
		}

		if ( StringUtils.hasText(value) ) {
			throw new IllegalArgumentException("value is null");
		}

		ResponseCookie cookie = ResponseCookie.from(value, value)
				.path("/")
				.maxAge(maxAge)
				.secure(isSecure)
				.httpOnly(isHttpOnly)
				.domain( StringUtils.hasText(domain) ? domain : "" )
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
