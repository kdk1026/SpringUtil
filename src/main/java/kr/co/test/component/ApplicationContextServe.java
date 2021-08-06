package kr.co.test.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 6. 김대광	최초작성
 * </pre>
 * 
 *
 * @author 김대광
 */
@Component
public class ApplicationContextServe implements ApplicationContextAware {

	private static ApplicationContext context;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	/**
	 * @return the context
	 */
	public static ApplicationContext getContext() {
		return context;
	}

}

