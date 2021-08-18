package kr.co.test.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 30. 김대광	최초작성
 * </pre>
 * 
 * @see <a href="https://offbyone.tistory.com/144">Ref</a>
 * @author 김대광
 */
public class ContextUtil {

	/**
	 * 외부에서 객체 인스턴스화 불가
	 */
	private ContextUtil() {
		super();
	}
	
	private static class LazyHolder {
		private static final ContextUtil INSTANCE = new ContextUtil();
	}

	public static ContextUtil getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * 빈을 직접 얻습니다.
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		return context.getBean(beanName);
	}
	
	/**
	 * HttpServletReqeust 객체를 직접 얻습니다.
	 * @return
	 */
	public HttpServletRequest getRequest() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getRequest();
	}
	
	/**
	 * HttpServletResponse 객체를 직접 얻습니다.
	 * @return
	 */
	public HttpServletResponse getResponse() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getResponse();
	}
	
	/**
	 * HttpSession 객체를 직접 얻습니다.
	 * @param create
	 * @return
	 */
	public HttpSession getSession(boolean create) {
		return this.getRequest().getSession(create);
	}
			
	/**
	 * REQUEST 영역에서 가져오기
	 * @param key
	 * @return
	 */
	public Object getAttrFromRequest(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, ServletRequestAttributes.SCOPE_REQUEST);
	}
	
	/**
	 * REQUEST 영역에 객체 저장
	 * @param key
	 * @param obj
	 */
	public void setAttrToRequest(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_REQUEST);
	}
	
	/**
	 * SESSION 영역에 가져오기
	 * @param key
	 * @return
	 */
	public Object getAttrFromSession(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, ServletRequestAttributes.SCOPE_SESSION);
	}
	
	/**
	 * SESSION 영역에 객체 저장
	 * @param key
	 * @return
	 */
	public void setAttrToSession(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_SESSION);
	}
}
