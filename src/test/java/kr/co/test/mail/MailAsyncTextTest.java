package kr.co.test.mail;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
class MailAsyncTextTest {

	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Test
	void test() throws IllegalStateException, InterruptedException, MessagingException {
		String mailTo = "kdk1026@naver.com";
		String mailSubject = "메일 테스트 (텍스트)";
		String mailMsg = "테스트 12345";

		// XXX : 음... 비동기는 단위 테스트 방법이 독특한듯.. 컨트롤러에서 서비스 호출하게 해놓고, 웹에서 호출하면 돌거 같다...
		mailSenderComponent.asyncSendmail(false, mailTo, mailSubject, mailMsg);

		boolean awaitTermination = executor.getThreadPoolExecutor().awaitTermination(3, TimeUnit.SECONDS);
		assertTrue(awaitTermination);
	}

}
