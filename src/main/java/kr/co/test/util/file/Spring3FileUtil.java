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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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

	/**
	 * 폴더 구분자
	 */
	private static final String FOLDER_SEPARATOR = "/";

	/**
	 * 확장자 구분자
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	private Spring3FileUtil() {
		super();
	}

	private static class ExceptionMessage {

		private ExceptionMessage() {
		}

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String inValid(String paramName) {
			return String.format("'%s' is inValid", paramName);
		}

	}

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
	public static class FileVO {

		/**
		 * 파일 경로
		 */
		private String destFilePath;

		/**
		 * 파일 확장자
		 */
		private String fileExt;

		/**
		 * 원파일명
		 */
		private String orignlFileNm;

		/**
		 * 저장파일명
		 */
		private String saveFileNm;

		/**
		 * 파일 크기
		 */
		private long fileSize;

		/**
		 * 파일 크기 단위
		 */
		private String fileSizeUnits;
	}

	private static String checkDestFilePath(String destFilePath) {
		return (destFilePath.replaceAll("^(.*)(.$)", "$2").equals("/")) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);
	}

	/**
	 * <pre>
	 * Spring 3 파일 업로드
	 *  - Apache Commons IO 필요
	 *  - 파일 업로드 전 파일 확장자 및 MIME Type 체크 진행할 것
	 * </pre>
	 * @param multipartFile
	 * @param destFilePath
	 * @return
	 *
	 * <pre>
	 * {@code
	 * FileVO fileVo = Spring3FileUtil.uploadFile(multipartFile, destFilePath);
	 * }
	 * </pre>
	 */
	public static FileVO uploadFile(MultipartFile multipartFile, String destFilePath) {
		Objects.requireNonNull(multipartFile, ExceptionMessage.isNull("multipartFile"));

		if ( ObjectUtils.isEmpty(destFilePath.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.inValid("destFilePath"));
		}

		destFilePath = checkDestFilePath(destFilePath);
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
			if ( !targetFile.toPath().normalize().startsWith(destFilePath) ) {
				throw new IOException("Invalid file path");
	        }

			multipartFile.transferTo(targetFile);

			fileVO = FileVO.builder()
				.destFilePath(destFilePath)
				.fileExt(fileExt)
				.orignlFileNm(multipartFile.getOriginalFilename())
				.saveFileNm(saveFileNm)
				.fileSize(multipartFile.getSize())
				.fileSizeUnits(InnerFileutils.readableFileSize(multipartFile.getSize()))
				.build();

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
	 *
	 * <pre>
	 * {@code
	 * FileVO fileVo = FileVO.builder()
	 * 	.destFilePath(destFilePath)
	 * 	.saveFileNm(saveFileNm)
	 * 	.orignlFileNm(orignlFileNm)
	 * 	.build();
	 * }
	 * </pre>
	 */
	public static void downloadFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		Objects.requireNonNull(fileVO, ExceptionMessage.isNull("fileVO"));
		checkRequest(request);
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = checkDestFilePath(destFilePath);

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
	 *
	 * <pre>
	 * {@code
	 * FileVO fileVo = FileVO.builder()
	 * 	.destFilePath(destFilePath)
	 * 	.saveFileNm(saveFileNm)
	 * 	.orignlFileNm(orignlFileNm)
	 * 	.build();
	 * }
	 * </pre>
	 */
	public static void openPdfFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		Objects.requireNonNull(fileVO, ExceptionMessage.isNull("fileVO"));
		checkRequest(request);
		Objects.requireNonNull(response, ExceptionMessage.isNull("response"));

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = checkDestFilePath(destFilePath);

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
		checkRequest(request);

		if ( ObjectUtils.isEmpty(str.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.inValid("str"));
		}

		String fileName = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				fileName = URLEncoder.encode(str, StandardCharsets.UTF_8.toString()).replace("\\+", " ");
			} else {
				// 브라우저에서는 처리되지만 Swagger에서는 한글 깨짐
				// fileName = new String(str.getBytes(StandardCharsets.UTF_8.toString()), StandardCharsets.ISO_8859_1);

				fileName = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}

		return fileName;
	}

	private static void checkRequest(HttpServletRequest request) {
		Objects.requireNonNull(request, ExceptionMessage.isNull("request"));
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
