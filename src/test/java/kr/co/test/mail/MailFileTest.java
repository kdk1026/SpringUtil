package kr.co.test.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

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
public class MailFileTest {
	
	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Test
	public void test() {
		String mailTo = "daekwang1026@gmail.com";
		String mailSubject = "메일 테스트 (File)";
		String mailMsg = "<h3>테스트 12345</h3>";
		
//		System.out.println( mailSenderComponent.sendmail(false, mailTo, mailSubject, mailMsg) );
		
		File file = new File("c:/test/test.html");
		
		assertTrue( mailSenderComponent.sendmailWithAttachFile(true, mailTo, mailSubject, mailMsg, file) );
	}
	
}
