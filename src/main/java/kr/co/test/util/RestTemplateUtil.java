package kr.co.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2019. 4. 27. 김대광	최초작성
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 22. 김대광	AI에게 검토 요청해서 개선
 * </pre>
 *
 * <pre>
 * Spring 전용 Http Client
 *  - Dependency
 *    > Apache HttpClient
 *    > Jackson
 * </pre>
 *
 * isSSL은 false로 해서 오류 나는 경우에만 true로 사용
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

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	private RestTemplateUtil() {
		super();
	}

	private static class ExceptionMessage {

		private ExceptionMessage() {
		}

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

	}

	private static final int TIMEOUT = 5000;

	// 일반용 (isVerify = true)
	private static final RestTemplate VERIFIED_TEMPLATE = new RestTemplate();

	// SSL 무시용 (isVerify = false)
	private static final RestTemplate UNVERIFIED_TEMPLATE = createUnverifiedTemplate();

    public static RestTemplate getRestTemplate(boolean isVerify) {
        return isVerify ? VERIFIED_TEMPLATE : UNVERIFIED_TEMPLATE;
    }

    private static RestTemplate createUnverifiedTemplate() {
    	try {
    		// 모든 인증서를 신뢰하는 SSLContext 생성
    		SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

    		// SSL 체크를 하지 않는 HttpClient 구성
    		CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(5)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(TIMEOUT)
                            .setSocketTimeout(TIMEOUT)
                            .setConnectionRequestTimeout(TIMEOUT)
                            .build())
                    .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		} catch (Exception e) {
			logger.error("RestTemplate 생성 실패", e);
			return new RestTemplate();
		}
    }

	private static class Convert {
		@SuppressWarnings("unchecked")
		private static Map<String, Object> objectToMap(Object obj) {
			Map<String, Object> map = null;

			ObjectMapper oMapper = new ObjectMapper();
			map = oMapper.convertValue(obj, Map.class);

			return map;
		}

		private static MultiValueMap<String, String> mapToHttpHeaders(Map<String, Object> headerMap, HttpHeaders headers) {
			MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();

			MediaType mediaType = headers.getContentType();
			if ( mediaType != null ) {
				mMap.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());
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

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> get(boolean isSSL, String url, MediaType mediaType
			, Map<String, Object> headerMap, Class<?> responseType, Object... uriVariables) {

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));

		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isSSL);

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

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));


		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isSSL);

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
