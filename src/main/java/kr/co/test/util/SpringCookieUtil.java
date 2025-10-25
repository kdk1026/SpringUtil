package kr.co.test.util;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.ObjectUtils;
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

	private static final String LOCAL_PROFILE = "local";

	private SpringCookieUtil() {
		super();
	}

	private static class ExceptionMessage {

		private ExceptionMessage() {
		}

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	/**
	 * Spring 쿠키 설정
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param domain
	 * @param profile
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain, String profile) {
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( ObjectUtils.isEmpty(name.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("name"));
		}

		if ( ObjectUtils.isEmpty(value.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("value"));
		}

		ResponseCookie cookie = ResponseCookie.from(value, value)
				.path("/")
				.maxAge(maxAge)
				.httpOnly(true)
				.secure(!LOCAL_PROFILE.equals(profile))
				.domain(StringUtils.hasText(domain) ? domain : "")
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	/**
	 * Spring 세션 쿠키 설정
	 * @param response
	 * @param name
	 * @param value
	 * @param domain
	 * @param profile
	 */
	public static void addSessionCookie(HttpServletResponse response, String name, String value, String domain, String profile) {
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		if ( ObjectUtils.isEmpty(name.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("name"));
		}

		if ( ObjectUtils.isEmpty(value.trim()) ) {
			throw new IllegalArgumentException("value must not be null");
		}

		ResponseCookie cookie = ResponseCookie.from(value, value)
				.path("/")
				.httpOnly(true)
				.secure(!LOCAL_PROFILE.equals(profile))
				.domain(StringUtils.hasText(domain) ? domain : "")
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
