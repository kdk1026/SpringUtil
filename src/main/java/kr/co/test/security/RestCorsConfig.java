package kr.co.test.security;

import javax.servlet.http.HttpServletRequest;
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
		return new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cors = new CorsConfiguration();
				cors.addAllowedOrigin("*");
				cors.addAllowedMethod("POST, GET");
				cors.setMaxAge(3600L);
				cors.addAllowedHeader("Content-Type, Accept, x-requested-with, Authorization");
				return cors;
			}
		};
	}
	
}
