package kr.co.test.mail;

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
public class MailAsyncTextTest {
	
	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Test
	public void test() {
		String mailTo = "daekwang1026@gmail.com";
		String mailSubject = "메일 테스트 (텍스트)";
		String mailMsg = "테스트 12345";
		
		mailSenderComponent.sendmailAsync(false, mailTo, mailSubject, mailMsg);
	}
	
}
