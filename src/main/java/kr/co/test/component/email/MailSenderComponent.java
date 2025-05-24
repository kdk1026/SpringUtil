package kr.co.test.component.email;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;

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

	/**
	 * 일반 메일 발송
	 * @param html
	 * @param mailTo
	 * @param mailSubject
	 * @param mailMsg
	 * @return
	 */
	public boolean sendmail(boolean html, String mailTo, String mailSubject, String mailMsg);

	/**
	 * 대량 메일 발송
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @return
	 */
	public boolean sendMailMulti(boolean html, List<String> mailToList, String mailSubject, String mailMsg);

	/**
	 * <pre>
	 * 대량 메일 발송
	 *   - 개별 발송 처리
	 *   - 성공/실패/처리 개수, 실패한 수신자 메일 리스트
	 * </pre>
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @return
	 */
	public MailMultiSendResult sendmailResult(boolean html, List<String> mailToList, String mailSubject, String mailMsg);

	/**
	 * 첨부 파일 일반 메일 발송
	 * @param html
	 * @param mailTo
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 * @return
	 */
	public boolean sendmailWithAttachFile(boolean html, String mailTo, String mailSubject, String mailMsg, File attachFile);

	/**
	 * 첨부 파일 대량 메일 발송
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 * @return
	 */
	public boolean sendmailWithAttachFile(boolean html, List<String> mailToList, String mailSubject, String mailMsg, File attachFile);

	/**
	 * <pre>
	 * 첨부 파일 대량 메일 발송
	 *   - 개별 발송 처리
	 *   - 성공/실패/처리 개수, 실패한 수신자 메일 리스트
	 * </pre>
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 * @return
	 */
	public MailMultiSendResult sendmailWithAttachFileResult(boolean html, List<String> mailToList, String mailSubject, String mailMsg, File attachFile);

	/**
	 * <비동기> 일반 메일 발송
	 * @param html
	 * @param mailTo
	 * @param mailSubject
	 * @param mailMsg
	 * @throws MessagingException
	 */
	public void asyncSendmail(boolean html, String mailTo, String mailSubject, String mailMsg) throws MessagingException;

	/**
	 * <비동기> 대량 메일 발송
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @throws MessagingException
	 */
	public void asyncSendmail(boolean html, List<String> mailToList, String mailSubject, String mailMsg) throws MessagingException;

	/**
	 * <비동기> 첨부 파일 일반 메일 발송
	 * @param html
	 * @param mailTo
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 * @throws MessagingException
	 */
	public void asyncSendmailWithAttachFile(boolean html, String mailTo, String mailSubject, String mailMsg, File attachFile) throws MessagingException;

	/**
	 * <비동기> 첨부 파일 대량 메일 발송
	 * @param html
	 * @param mailToList
	 * @param mailSubject
	 * @param mailMsg
	 * @param attachFile
	 * @throws MessagingException
	 */
	public void asyncSendmailWithAttachFile(boolean html, List<String> mailToList, String mailSubject, String mailMsg, File attachFile) throws MessagingException;

}
