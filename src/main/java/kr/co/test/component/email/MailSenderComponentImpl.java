package kr.co.test.component.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 31. 김대광	최초작성
 * </pre>
 *
 * Java 에서 메일 발송 시, CSS 적용해야 한다면 반드시 inline으로 써야 한다고 한다.
 * @author 김대광
 */
@Component
public class MailSenderComponentImpl implements MailSenderComponent {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private JavaMailSender mailSender;

	public MailSenderComponentImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Value("${spring.mail.from}")
	private String mailFrom;

	private static final String FAILED_READ_ATTACHMENT_FILE = "Failed to read attachment file: {}";

	 /**
	 * <pre>
	 * -----------------------------------
	 * 개정이력
	 * -----------------------------------
	 * 2026. 1. 7. 김대광	최초작성
	 * </pre>
	 *
	 * <pre>
	 * 1) static class의 protected 변수는 동일 패키지 경로 아니면 접근 불가
	 * 2) static class의 public 변수(C의 구조체와 유사 형태)는 SonarLint가 지적하므로 LomBok 이용한 Builder 패턴 스타일로 처리
	 * - Make destFilePath a static final constant or non-public and provide accessors if needed.
	 * </pre>
	 *
	 * @author 김대광
	 */
	@Getter
	@Builder
	@ToString
	public static class MailMultiSendResult {
		/** 성공 개수 */
		private int successCnt;

		/** 실패 개수 */
		private int failureCnt;

		/** 처리 개수 */
		private int processCnt;

		private List<String> failureMailTos;
	}

	@Override
	public boolean sendmail(boolean html, String mailTo, String mailSubject, String mailMsg) {
		return this.sendmailWithAttachFile(html, mailTo, mailSubject, mailMsg, null);
	}

	@Override
	public boolean sendMailMulti(boolean html, List<String> mailToList, String mailSubject, String mailMsg) {
		return this.sendmailWithAttachFile(html, mailToList, mailSubject, mailMsg, null);
	}

	@Override
	public MailMultiSendResult sendmailResult(boolean html, List<String> mailToList, String mailSubject, String mailMsg) {
		return this.sendmailWithAttachFileResult(html, mailToList, mailSubject, mailMsg, null);
	}

	@Override
	public boolean sendmailWithAttachFile(boolean html, String mailTo, String mailSubject, String mailMsg,
			File attachFile) {
		boolean isSuccess = false;

		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, (attachFile != null) );

			helper.setFrom(this.mailFrom);
			helper.setTo(mailTo);
			helper.setSubject(mailSubject);
			helper.setText(mailMsg, html);

			if ( attachFile != null ) {
				try ( InputStream is = new FileInputStream(attachFile) ) {
					InputStreamResource inputStreamResource = new InputStreamResource(is);
					helper.addAttachment(attachFile.getName(), inputStreamResource);
				}
			}

			mailSender.send(message);

			isSuccess = true;

        } catch (MessagingException e) {
            logger.error("Error sending mail: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error(FAILED_READ_ATTACHMENT_FILE, (attachFile != null ? attachFile.getName() : "N/A"), e);
		} catch (Exception e) {
			logger.error("", e);
		}

		return isSuccess;
	}

	@Override
	public boolean sendmailWithAttachFile(boolean html, List<String> mailToList, String mailSubject, String mailMsg,
			File attachFile) {
		boolean isSuccess = false;

		String[] mailTos = mailToList.toArray( new String[mailToList.size()] );
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, (attachFile != null) );

			helper.setFrom(this.mailFrom);
			helper.setTo(mailTos);
			helper.setSubject(mailSubject);
			helper.setText(mailMsg, html);

			if ( attachFile != null ) {
				try ( InputStream is = new FileInputStream(attachFile) ) {
					InputStreamResource inputStreamResource = new InputStreamResource(is);
					helper.addAttachment(attachFile.getName(), inputStreamResource);
				}
			}

			mailSender.send(message);

			isSuccess = true;

        } catch (MessagingException e) {
            logger.error("Error sending mail: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error(FAILED_READ_ATTACHMENT_FILE, (attachFile != null ? attachFile.getName() : "N/A"), e);
		} catch (Exception e) {
			logger.error("", e);
		}

		return isSuccess;
	}

	@Override
	public MailMultiSendResult sendmailWithAttachFileResult(boolean html, List<String> mailToList, String mailSubject,
			String mailMsg, File attachFile) {
		MailMultiSendResult mailMultiSendResult = null;

		int nSuccessCnt = 0;
		int nFailureCnt = 0;
		int nProcessCnt = 0;

		List<String> failureMailTos = new ArrayList<>();

		for (String mailTo : mailToList) {
			boolean isSuccess = this.sendmailWithAttachFile(html, mailTo, mailSubject, mailMsg, attachFile);

			if ( isSuccess ) {
				nSuccessCnt ++;
			} else {
				nFailureCnt ++;
				failureMailTos.add(mailTo);
			}

			nProcessCnt ++;
		}

		mailMultiSendResult = MailMultiSendResult.builder()
				.successCnt(nSuccessCnt)
				.failureCnt(nFailureCnt)
				.processCnt(nProcessCnt)
				.failureMailTos(failureMailTos)
				.build();

		return mailMultiSendResult;
	}

	@Async
	@Override
	public void asyncSendmail(boolean html, String mailTo, String mailSubject, String mailMsg) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(this.mailFrom);
		helper.setTo(mailTo);
		helper.setSubject(mailSubject);
		helper.setText(mailMsg, html);

		mailSender.send(message);
	}

	@Async
	@Override
	public void asyncSendmail(boolean html, List<String> mailToList, String mailSubject, String mailMsg) throws MessagingException {
		String[] mailTos = mailToList.toArray( new String[mailToList.size()] );
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(this.mailFrom);
		helper.setTo(mailTos);
		helper.setSubject(mailSubject);
		helper.setText(mailMsg, html);

		mailSender.send(message);
	}

	@Async
	@Override
	public void asyncSendmailWithAttachFile(boolean html, String mailTo, String mailSubject, String mailMsg,
			File attachFile) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, (attachFile != null) );

		helper.setFrom(this.mailFrom);
		helper.setTo(mailTo);
		helper.setSubject(mailSubject);
		helper.setText(mailMsg, html);

		if ( attachFile != null ) {
			try ( InputStream is = new FileInputStream(attachFile) ) {
				InputStreamResource inputStreamResource = new InputStreamResource(is);
				helper.addAttachment(attachFile.getName(), inputStreamResource);
			} catch (IOException e) {
				logger.error(FAILED_READ_ATTACHMENT_FILE, attachFile.getName(), e);
			}
		}

		mailSender.send(message);
	}

	@Async
	@Override
	public void asyncSendmailWithAttachFile(boolean html, List<String> mailToList, String mailSubject, String mailMsg,
			File attachFile) throws MessagingException {
		String[] mailTos = mailToList.toArray( new String[mailToList.size()] );
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, (attachFile != null) );

		helper.setFrom(this.mailFrom);
		helper.setTo(mailTos);
		helper.setSubject(mailSubject);
		helper.setText(mailMsg, html);

		if ( attachFile != null ) {
			try ( InputStream is = new FileInputStream(attachFile) ) {
				InputStreamResource inputStreamResource = new InputStreamResource(is);
				helper.addAttachment(attachFile.getName(), inputStreamResource);
			} catch (IOException e) {
				logger.error(FAILED_READ_ATTACHMENT_FILE, attachFile.getName(), e);
			}
		}

		mailSender.send(message);
	}

}
