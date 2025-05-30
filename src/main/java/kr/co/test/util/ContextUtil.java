package kr.co.test.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 30. 김대광	최초작성
 * 2021. 8. 19. 김대광	SonarLint 지시에 따른 수정
 * 2025. 5. 18. 김대광	AI가 추천한 Singleton 패턴으로 변경
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, static Class로 구분
 * </pre>
 *
 * @see <a href="https://offbyone.tistory.com/144">Ref</a>
 * @see <a href="https://github.com/kdk1026/BaseApi/blob/master/src/main/java/com/kdk/app/common/util/spring/ContextUtil.java">Boot 3.x 참고</a>
 * @author 김대광
 */
public class ContextUtil {

	private ContextUtil() {
		super();
	}

	/**
	 * 빈을 직접 얻습니다.
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		Objects.requireNonNull(beanName.trim(), ExceptionMessage.isNull("beanName"));

		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		return (context == null) ? null : context.getBean(beanName);
	}

	/**
	 * HttpServletResponse 객체를 직접 얻습니다.
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getResponse();
	}

	public static class Request {
		private Request() {
			super();
		}

		/**
		 * HttpServletReqeust 객체를 직접 얻습니다.
		 * @return
		 */
		public static HttpServletRequest getRequest() {
			ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			return attr.getRequest();
		}

		/**
		 * REQUEST 영역에서 가져오기
		 * @param key
		 * @return
		 */
		public static Object getAttrFromRequest(String key) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			return attr.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
		}

		/**
		 * REQUEST 영역에 객체 저장
		 * @param key
		 * @param obj
		 */
		public static void setAttrToRequest(String key, Object obj) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));
			Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			attr.setAttribute(key, obj, RequestAttributes.SCOPE_REQUEST);
		}

		/**
		 * REQUEST 영역에서 삭제
		 * @param key
		 */
		public static void removeAttrFromRequest(String key) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			attr.removeAttribute(key, RequestAttributes.SCOPE_REQUEST);
		}
	}

	public static class Session {
		private Session() {
			super();
		}

		/**
		 * HttpSession 객체를 직접 얻습니다.
		 * @param create
		 * @return
		 */
		public static HttpSession getSession(boolean create) {
			return ContextUtil.Request.getRequest().getSession(create);
		}

		/**
		 * SESSION 영역에서 가져오기
		 * @param key
		 * @return
		 */
		public static Object getAttrFromSession(String key) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			return attr.getAttribute(key, RequestAttributes.SCOPE_SESSION);
		}

		/**
		 * SESSION 영역에 객체 저장
		 * @param key
		 * @return
		 */
		public static void setAttrToSession(String key, Object obj) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));
			Objects.requireNonNull(obj, ExceptionMessage.isNull("obj"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			attr.setAttribute(key, obj, RequestAttributes.SCOPE_SESSION);
		}

		/**
		 * SESSION 영역에서 삭제
		 * @param key
		 */
		public static void removeAttrFromSession(String key) {
			Objects.requireNonNull(key.trim(), ExceptionMessage.isNull("key"));

			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			attr.removeAttribute(key, RequestAttributes.SCOPE_SESSION);
		}
	}

}
