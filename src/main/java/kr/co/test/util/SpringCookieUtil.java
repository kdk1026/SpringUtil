package kr.co.test.util;

import java.util.Objects;

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
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영
 * </pre>
 *
 * <pre>
 * Spring 5.0 부터 지원
 *   - 5.1 부터는 사용 빈도는 적으나 sameSite 설정 가능
 *   - <a href="https://cherish-it.tistory.com/12">sameSite 참고</a>
 * </pre>
 *
 * @see <a href="https://github.com/kdk1026/BaseApi/blob/master/src/main/java/com/kdk/app/common/util/spring/SpringCookieUtil.java">Boot 3.x 참고</a>
 * @author kdk
 */
public class SpringCookieUtil {

	private SpringCookieUtil() {
		super();
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
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isSecure, boolean isHttpOnly, String domain) {
		Objects.requireNonNull(response, "response must not be null");
		Objects.requireNonNull(name.trim(), "name must not be null");
		Objects.requireNonNull(value.trim(), "value must not be null");

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
