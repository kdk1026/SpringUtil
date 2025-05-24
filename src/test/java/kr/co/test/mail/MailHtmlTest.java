package kr.co.test.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.test.component.email.MailSenderComponent;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 31. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@SpringBootTest
class MailHtmlTest {

	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Test
	void test() {
		String mailTo = "daekwang1026@gmail.com";
		String mailSubject = "메일 테스트 (HTML)";
		String mailMsg = "<h3>테스트 12345</h3>";

//		System.out.println( mailSenderComponent.sendmail(false, mailTo, mailSubject, mailMsg) );
		assertTrue( mailSenderComponent.sendmail(true, mailTo, mailSubject, mailMsg) );
	}

}
