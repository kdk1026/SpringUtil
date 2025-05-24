package kr.co.test.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
class MailMultiTextTest {

	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Test
	void test() {
		List<String> mailTos = new ArrayList<>();
		mailTos.add("daekwang1026@gmail.com");
		mailTos.add("kdk1026@naver.com");

		String mailSubject = "메일 테스트 (텍스트)";
		String mailMsg = "테스트 12345";

		assertTrue(mailSenderComponent.sendMailMulti(true, mailTos, mailSubject, mailMsg));
	}

}
