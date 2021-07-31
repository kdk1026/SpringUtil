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
		
		// TODO : 얼라료? 발송이 안된다... 구름인가 눈은가 저 높은곳 킬리만자로... 나중에 연구하자... XML 시절 프로젝트 소스 까봐도 맞는데...설정 문제같은데...
		mailSenderComponent.sendmailAsync(false, mailTo, mailSubject, mailMsg);
	}
	
}
