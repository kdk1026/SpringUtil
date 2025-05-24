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
		/*
		 * 이런 옘병할 언제 바뀐겨... 왜 타입을 바꾸가 난리야..
		 * 내가 Java 8 이후에서 한 일이 없었구나... 아니면 Oracle JDK가 아니라서??? 모르겠다...
		 * 요로나 저라나 닝기미 빠빠빠
		 */
		//List<Object> mailTos = Arrays.asList( new String[] {"daekwang1026@gmail.com", "kdk1026@naver.com"} );

		List<String> mailTos = new ArrayList<>();
		mailTos.add("daekwang1026@gmail.com");
		mailTos.add("kdk1026@naver.com");

		String mailSubject = "메일 테스트 (텍스트)";
		String mailMsg = "테스트 12345";

		assertTrue(mailSenderComponent.sendMailMulti(true, mailTos, mailSubject, mailMsg));
	}

}
