package kr.co.test.util.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.co.test.util.ExceptionMessage;

/**
 * <pre>
 * 개정이력
 * -----------------------------------
 * 2021. 7. 30. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * 업로드 시, 의존성 라이브러리 필요 하다는게 단점
 * 익숙하단게 장점
 * Boot 프로젝트 생성 시, 의존성 라이브러리 자동으로 가지고옴
 * </pre>
 * @author 김대광
 */
public class Spring3FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(Spring3FileUtil.class);

	private Spring3FileUtil() {
		super();
	}

	/**
	 * 폴더 구분자
	 */
	private static final String FOLDER_SEPARATOR = "/";

	/**
	 * 확장자 구분자
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	private static final String REGEX_EXTRACT_LAST_CHAR = "^(.*)(.$)";
	private static final String REPLACEMENT_LAST_CHAR = "$2";
	private static final String PATH_SEPARATOR = "/";

	private static final String REQUEST = "request";

	public static class FileVO implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 파일 경로
		 */
		protected String destFilePath;

		/**
		 * 파일 확장자
		 */
		protected String fileExt;

		/**
		 * 원파일명
		 */
		protected String orignlFileNm;

		/**
		 * 저장파일명
		 */
		protected String saveFileNm;

		/**
		 * 파일 크기
		 */
		protected long fileSize;

		/**
		 * 파일 크기 단위
		 */
		protected String fileSizeUnits;

		public String toString() {
			return ToStringBuilder.reflectionToString(
				this, ToStringStyle.MULTI_LINE_STYLE
			);
		}
	}

	/**
	 * <pre>
	 * Spring 3 파일 업로드
	 *  - Apache Commons IO 필요
	 *  - 파일 업로드 전 파일 확장자 및 MIME Type 체크 진행할 것
	 * </pre>
	 * @param multipartFile
	 * @param env
	 * @return
	 */
	public static FileVO uploadFile(MultipartFile multipartFile, Environment env) {
		Objects.requireNonNull(multipartFile, ExceptionMessage.isNull("multipartFile"));
		Objects.requireNonNull(env, ExceptionMessage.isNull("env"));

		String destFilePath = env.getProperty("file.upload.path");
		Objects.requireNonNull(destFilePath, ExceptionMessage.isNull("file.upload.path"));

		destFilePath = (destFilePath.replaceAll(REGEX_EXTRACT_LAST_CHAR, REPLACEMENT_LAST_CHAR).equals(PATH_SEPARATOR)) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);
		File destFile = new File(destFilePath);
		if (!destFile.exists()) {
			destFile.mkdirs();
		}

		StringBuilder sb = new StringBuilder();
		String fileExt = EXTENSION_SEPARATOR + InnerFileutils.getFileExtension(multipartFile.getOriginalFilename());

		sb.append( UUID.randomUUID().toString().replace("-", "") ).append(fileExt);
		String saveFileNm = sb.toString();

		sb.setLength(0);
		sb.append(destFilePath).append(FOLDER_SEPARATOR).append(saveFileNm);

		File targetFile = new File(sb.toString());
		FileVO fileVO = null;

		try {
			multipartFile.transferTo(targetFile);

			fileVO = new FileVO();
			fileVO.destFilePath = destFilePath;
			fileVO.orignlFileNm = multipartFile.getOriginalFilename();
			fileVO.saveFileNm = saveFileNm;
			fileVO.fileExt = fileExt;
			fileVO.fileSize = multipartFile.getSize();
			fileVO.fileSizeUnits = InnerFileutils.readableFileSize(fileVO.fileSize);

		} catch (IllegalStateException | IOException e) {
			logger.error("", e);
		}

		return fileVO;
	}

	/**
	 * Spring 3 파일 다운로드
	 * @param fileVO
	 * @param request
	 * @param response
	 */
	public static void downloadFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		Objects.requireNonNull(fileVO, ExceptionMessage.isNull("fileVO"));
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = (destFilePath.replaceAll(REGEX_EXTRACT_LAST_CHAR, REPLACEMENT_LAST_CHAR).equals(PATH_SEPARATOR)) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);

		String saveFileNm = fileVO.saveFileNm;
		String orignlFileNm = fileVO.orignlFileNm;

		if ( !StringUtils.hasText(orignlFileNm) ) {
			downloadlFileNm = contentDisposition(request, saveFileNm);
		} else {
			downloadlFileNm = contentDisposition(request, orignlFileNm);
		}

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Transfer-Encoding", "binary;");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadlFileNm + "\";");

		try (
				FileInputStream fis = new FileInputStream(destFilePath + saveFileNm);
				InputStream is = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
        ) {
			FileCopyUtils.copy(is, os);
		} catch (IllegalStateException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * Spring 3 PDF 파일 열기
	 * @param fileVO
	 * @param request
	 * @param response
	 */
	public static void openPdfFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		Objects.requireNonNull(fileVO, ExceptionMessage.isNull("fileVO"));
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = (destFilePath.replaceAll(REGEX_EXTRACT_LAST_CHAR, REPLACEMENT_LAST_CHAR).equals(PATH_SEPARATOR)) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);

		String saveFileNm = fileVO.saveFileNm;
		String orignlFileNm = fileVO.orignlFileNm;

		if ( !StringUtils.hasText(orignlFileNm) ) {
			downloadlFileNm = contentDisposition(request, saveFileNm);
		} else {
			downloadlFileNm = contentDisposition(request, orignlFileNm);
		}

		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-Disposition", "inline; attachment; filename=\"" + downloadlFileNm + "\";");

		try (
				FileInputStream fis = new FileInputStream(destFilePath + saveFileNm);
				InputStream is = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
        ) {
			FileCopyUtils.copy(is, os);
		}  catch (IllegalStateException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * <pre>
	 * 브라우저에 따른 인코딩 설정
	 *   - str은 다운로드할 파일명이나 출력할 문자열
	 * </pre>
	 * @param request
	 * @param str
	 * @return
	 */
	public static String contentDisposition(HttpServletRequest request, String str) {
		Objects.requireNonNull(request, ExceptionMessage.isNull(REQUEST));
		Objects.requireNonNull(str.trim(), ExceptionMessage.isNull("str"));

		String sRes = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				sRes = URLEncoder.encode(str, StandardCharsets.UTF_8.toString()).replace("\\+", " ");
			} else {
				sRes = new String(str.getBytes(StandardCharsets.UTF_8.toString()), StandardCharsets.ISO_8859_1);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return sRes;
	}

	private static class InnerFileutils implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 파일 확장자 구하기
		 * @param fileName
		 * @return
		 */
		public static String getFileExtension(String fileName) {
			Objects.requireNonNull(fileName, ExceptionMessage.isNull("fileName"));

			if (fileName.lastIndexOf(EXTENSION_SEPARATOR) == -1) {
				return null;
			}
			int pos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
			return fileName.substring(pos + 1);
		}

		/**
		 * <pre>
		 * 파일 용량 구하기
		 *   - B, KB, MB, GB, TB
		 * </pre>
		 * @param size
		 * @return
		 */
		public static String readableFileSize(long fileSize) {
			final DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");

			if (fileSize < 0) {
				throw new IllegalArgumentException(ExceptionMessage.inValid("fileSize"));
			}

			if (fileSize == 0) return "0 B";
			String[] units = { "B", "KB", "MB", "GB", "TB" };

			int digitGroups = Math.min((int) (Math.log10(fileSize) / Math.log10(1024)), units.length - 1);
		    return decimalFormat.format(fileSize/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}
	}

}
