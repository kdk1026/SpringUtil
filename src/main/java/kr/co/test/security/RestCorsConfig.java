package kr.co.test.security;

import javax.swing.Spring;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 31. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * SecutiryConfig에 등록
 * 	http.cors().configurationSource(RestCorsConfig.configurationSource());
 *
 * Security 사용 시, MVC Config에서 다음 설정은 했더라도 Seuciry에 별도 등록을 해줘야 하나보다...
 * 	addCorsMappings @Override
 *
 * @see <a href="https://www.it-swarm-korea.com/ko/javascript/spring-boot-spring-security-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98%EC%97%90%EC%84%9C-cors%EB%A5%BC-%EA%B5%AC%EC%84%B1%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95%EC%9D%80-%EB%AC%B4%EC%97%87%EC%9E%85%EB%8B%88%EA%B9%8C/824690731/">Ref</a>
 * </pre>
 *
 * @see Spring 4.2 이상
 * @author 김대광
 */
public class RestCorsConfig {

	private RestCorsConfig() {
		super();
	}

	public static CorsConfigurationSource configurationSource() {
        return request -> {
            CorsConfiguration cors = new CorsConfiguration();
            cors.addAllowedOrigin("*");
            cors.addAllowedMethod("POST"); // 메서드를 개별적으로 추가하는 것이 더 명확하고 일반적입니다.
            cors.addAllowedMethod("GET");
            // cors.addAllowedMethod("POST, GET"); // 이 방식도 동작은 합니다.
            cors.setMaxAge(3600L);
            cors.addAllowedHeader("Content-Type");
            cors.addAllowedHeader("Accept");
            cors.addAllowedHeader("X-Requested-With"); // 일반적으로 HTTP 헤더는 대소문자를 구분하지 않지만, 관례상 '-'가 포함된 경우 CamelCase를 사용합니다.
            cors.addAllowedHeader("Authorization");
            return cors;
        };
	}

}
