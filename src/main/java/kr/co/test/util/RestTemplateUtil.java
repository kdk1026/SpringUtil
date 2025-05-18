package kr.co.test.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 4. 27. 김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * </pre>
 *
 * <pre>
 * Spring 전용 Http Client
 *  - Dependency
 *    > Apache HttpClient
 *    > Jackson
 * </pre>
 *
 * isSSL은 false로 해서 오류 나는 겨우에만 true로 사용
 *
 * <pre>
 * Spring Boot 3.x (Spring 6.x) 참조
 *   - https://github.com/kdk1026/BaseApi/blob/master/src/main/java/com/kdk/app/common/util/spring/RestTemplateUtil.java
 *   - https://github.com/kdk1026/BaseFront/blob/master/src/main/java/com/kdk/app/common/util/spring/RestTemplateUtil.java
 * </pre>
 *
 * @author 김대광
 */
public class RestTemplateUtil {

	private static RestTemplate instance;

	private RestTemplateUtil() {
		super();
	}

	private static class Config {
		private static class HttpClientConfig {
			private static final int TIMEOUT = 5000;

			private static final RequestConfig config =
					RequestConfig.custom()
					.setConnectTimeout(TIMEOUT)
					.setConnectionRequestTimeout(TIMEOUT)
					.setSocketTimeout(TIMEOUT)
					.build();
		}

		private static class HttpClientInstance {
			private static CloseableHttpClient getHttpClient(boolean isSSL) {
				CloseableHttpClient httpClient = null;

				/*
				 * connection pool 적용
				 *  - setMaxConnTotal		: 오픈되는 최대 커넥션 수 제한
				 *  - setMaxConnPerRoute	: IP, 포트 1쌍에 대해 수행 할 연결 수 제한
				 */

				if (isSSL) {
					httpClient = HttpClients.custom()
						.setDefaultRequestConfig(HttpClientConfig.config)
						.setSSLHostnameVerifier(new NoopHostnameVerifier())
						.setMaxConnTotal(100)
						.setMaxConnPerRoute(5)
						.build();
				} else {
					httpClient = HttpClientBuilder.create()
							.setDefaultRequestConfig(HttpClientConfig.config)
							.setMaxConnTotal(100)
							.setMaxConnPerRoute(5)
							.build();
				}

				return httpClient;
			}
		}

		private static class HttpRequestFactory {
			private static HttpComponentsClientHttpRequestFactory getRequestFactory(boolean isSSL) {
				return new HttpComponentsClientHttpRequestFactory(HttpClientInstance.getHttpClient(isSSL));
			}
		}
	}

	private static class Convert {
		@SuppressWarnings("unchecked")
		private static Map<String, Object> objectToMap(Object obj) {
			Map<String, Object> map = new HashMap<>();

			ObjectMapper oMapper = new ObjectMapper();
			map = oMapper.convertValue(obj, Map.class);

			return map;
		}

		private static MultiValueMap<String, String> mapToHttpHeaders(Map<String, Object> headerMap, HttpHeaders headers) {
			MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();

			if ( headers.getContentType() != null ) {
				mMap.add(HttpHeaders.CONTENT_TYPE, headers.getContentType().toString());
			}

			if ( headerMap != null ) {
				Iterator<String> it = headerMap.keySet().iterator();

				while ( it.hasNext() ) {
					String sKey = it.next();
					Object value = headerMap.get(sKey);

					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}

		private static MultiValueMap<String, Object> hashMapToMultiValueMap(Map<String, Object> map) throws IOException {
			MultiValueMap<String, Object> mMap = new LinkedMultiValueMap<>();

			Iterator<String> it = map.keySet().iterator();
			while ( it.hasNext() ) {
				String sKey = it.next();
				Object value = map.get(sKey);

				if ( value instanceof List<?> ) {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) value;
					mMap.put(sKey, list);

				} else if ( value instanceof File ) {
					File file = (File) value;
					mMap.add(sKey, new FileSystemResource(file));

				} else if ( value instanceof MultipartFile ) {
					final MultipartFile mFile = (MultipartFile) value;
					mMap.add(sKey, new ByteArrayResource(mFile.getBytes()) {

						@Override
						public String getFilename() {
							return mFile.getOriginalFilename();
						}
					});

				} else {
					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}
	}

	/**
	 * Singleton 인스턴스 생성
	 *
	 * @return
	 */
	private static synchronized RestTemplate getInstance(boolean isSSL) {
		if (instance == null) {
			instance = new RestTemplate(Config.HttpRequestFactory.getRequestFactory(isSSL));
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> get(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Class<?> responseType, Object... uriVariables) {

		RestTemplate restTemplate = RestTemplateUtil.getInstance(isSSL);

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = new HttpEntity<>(headers);

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType);
		}
	}

	public static ResponseEntity<Object> post(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Object body, Class<?> responseType, Object... uriVariables) throws IOException {

		Map<String, Object> bodyMap = Convert.objectToMap(body);
		return post(isSSL, url, mediaType, headerMap, bodyMap, responseType, uriVariables);
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> post(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Map<String, Object> bodyMap, Class<?> responseType, Object... uriVariables) throws IOException {

		RestTemplate restTemplate = RestTemplateUtil.getInstance(isSSL);

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = null;
		MultiValueMap<String, Object> mMap = null;

		if ( bodyMap != null ) {
			if ( mediaType == null || MediaType.APPLICATION_FORM_URLENCODED.equals(mediaType) || MediaType.MULTIPART_FORM_DATA.equals(mediaType) ) {
				mMap = Convert.hashMapToMultiValueMap(bodyMap);
				request = new HttpEntity<>(mMap, headers);

			} else {
				request = new HttpEntity<>(bodyMap, headers);
			}
		} else {
			request = new HttpEntity<>(headers);
		}

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType);
		}
	}

}
