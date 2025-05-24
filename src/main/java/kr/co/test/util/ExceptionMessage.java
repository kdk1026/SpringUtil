package kr.co.test.util;

 /**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 5. 24. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class ExceptionMessage {

	private ExceptionMessage() {
	}

	public static String isNull(String paramName) {
        return String.format("'%s' is null", paramName);
    }

	public static String inValid(String paramName) {
		return String.format("'%s' is inValid", paramName);
	}

}
