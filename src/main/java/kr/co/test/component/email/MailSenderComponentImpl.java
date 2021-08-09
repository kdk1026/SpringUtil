package kr.co.test.component.email;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.from}")
	private String mailFrom;
	
	public static class MailMultiSendResult {
		/** 성공 개수 */
		public int successCnt;

		/** 실패 개수 */
		public int failureCnt;

		/** 처리 개수 */
		public int processCnt;
		
		public List<String> failureMailTos;
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	@Override
	public boolean sendmail(boolean html, String mailTo, String mailSubject, String mailMsg) {
		return this.sendmailWithAttachFile(html, mailTo, mailSubject, mailMsg, null);
	}

	@Override
	public boolean sendMail(boolean html, List<String> mailToList, String mailSubject, String mailMsg) {
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
				FileSystemResource fileResource = new FileSystemResource(attachFile);
				helper.addAttachment(fileResource.getFilename(), fileResource);
			}
			
			mailSender.send(message);
			
			isSuccess = true;
			
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
				FileSystemResource fileResource = new FileSystemResource(attachFile);
				helper.addAttachment(fileResource.getFilename(), fileResource);
			}
			
			mailSender.send(message);
			
			isSuccess = true;
			
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return isSuccess;
	}
	
	@Override
	public MailMultiSendResult sendmailWithAttachFileResult(boolean html, List<String> mailToList, String mailSubject,
			String mailMsg, File attachFile) {
		MailMultiSendResult mailMultiSendResult = new MailMultiSendResult();
		
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
		
		mailMultiSendResult.successCnt = nSuccessCnt;
		mailMultiSendResult.failureCnt = nFailureCnt;
		mailMultiSendResult.processCnt = nProcessCnt;
		mailMultiSendResult.failureMailTos = failureMailTos;
		
		return mailMultiSendResult;
	}

	@Async
	@Override
	public void asyncSendmail(boolean html, String mailTo, String mailSubject, String mailMsg) throws MessagingException {
		this.asyncSendmailWithAttachFile(html, mailTo, mailSubject, mailMsg, null);
	}

	@Async
	@Override
	public void asyncSendmail(boolean html, List<String> mailToList, String mailSubject, String mailMsg) throws MessagingException {
		this.asyncSendmailWithAttachFile(html, mailToList, mailSubject, mailMsg, null);
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
			FileSystemResource fileResource = new FileSystemResource(attachFile);
			helper.addAttachment(fileResource.getFilename(), fileResource);
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
			FileSystemResource fileResource = new FileSystemResource(attachFile);
			helper.addAttachment(fileResource.getFilename(), fileResource);
		}
		
		mailSender.send(message);	
	}
	
}
