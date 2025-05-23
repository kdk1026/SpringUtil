package kr.co.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import kr.co.test.component.ApplicationContextServe;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 8. 6. 김대광	최초작성
 * </pre>
 *
 * ApplicationContextServe @Component 필요
 * @author 김대광
 */
public class SpringBootPropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootPropertyUtil.class);

	public static String getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}

	public static String getProperty(String propertyName, String defaultValue) {
		if ( StringUtils.hasText(propertyName) ) {
			throw new IllegalStateException("propertyName is null");
		}

		String value = "";

		ApplicationContext applicationContext = ApplicationContextServe.getContext();

		String property = applicationContext.getEnvironment().getProperty(propertyName);
		if ( property == null ) {
			logger.warn("{} properties was not loaded.", propertyName);
			value = defaultValue;
		} else {
			value = property.trim();
		}

		return value;
	}

}
