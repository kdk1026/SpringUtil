package kr.co.test.component.email;

import java.io.File;
import java.util.List;

import kr.co.test.component.email.MailSenderComponentImpl.MailMultiSendResult;

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
public interface MailSenderComponent {

	public boolean sendmail(boolean html, String mailTo, String mailSubject, String mailMsg);
	public MailMultiSendResult sendmail(boolean html, List<String> mailTos, String mailSubject, String mailMsg);
	
	public boolean sendmailWithAttachFile(boolean html, String mailTo, String mailSubject, String mailMsg, File attachFile);
	public MailMultiSendResult sendmailWithAttachFile(boolean html, List<String> mailTos, String mailSubject, String mailMsg, File attachFile);
	
	public void sendmailAsync(boolean html, String mailTo, String mailSubject, String mailMsg);
	
}
