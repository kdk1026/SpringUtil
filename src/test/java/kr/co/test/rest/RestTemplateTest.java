package kr.co.test.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import kr.co.test.util.RestTemplateUtil;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 22. kdk	최초작성
 * </pre>
 * Junit 이 맛이 가서...버전 충돌?
 *
 * @author kdk
 */
public class RestTemplateTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		final String BASE_URL = "https://jsonplaceholder.typicode.com/posts";

		String getUrl = BASE_URL + "/1";
		Map<String, Object> getMap = null;

		ResponseEntity<Object> getRes = RestTemplateUtil.get(false, getUrl, MediaType.APPLICATION_JSON, null, Map.class);
		if ( HttpStatus.OK == getRes.getStatusCode() ) {
			getMap = (Map<String, Object>) getRes.getBody();
		}
		System.out.println(getMap);
	}

}
