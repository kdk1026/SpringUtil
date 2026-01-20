package kr.co.test.util.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
 * 업로드 시, 의존성 라이브러리 필요 없다는게 장점
 * 익숙하지 않다는게 단점
 * </pre>
 * @author 김대광
 */
public class Spring4FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(Spring4FileUtil.class);

	/**
	 * 폴더 구분자
	 */
	private static final String FOLDER_SEPARATOR = "/";

	/**
	 * 확장자 구분자
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	private Spring4FileUtil() {
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
	 * Spring 4 파일 업로드
	 *  - Java 7 NIO API
	 *  - 파일 업로드 전 파일 확장자 및 MIME Type 체크 진행할 것
	 * </pre>
	 * @param multipartFile
	 * @param destFilePath
	 * @return
	 *
	 * <pre>
	 * {@code
	 * FileVO fileVo = Spring4FileUtil.uploadFile(multipartFile, destFilePath);
	 * }
	 * </pre>
	 */
	public static FileVO uploadFile(MultipartFile multipartFile, String destFilePath) {
		Objects.requireNonNull(multipartFile, ExceptionMessage.isNull("multipartFile"));

		if ( ObjectUtils.isEmpty(destFilePath.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.inValid("destFilePath"));
		}

		destFilePath = checkDestFilePath(destFilePath);

		Path destPath = Paths.get(destFilePath);

		if ( !destPath.toFile().exists() ) {
			try {
				Files.createDirectories(destPath);

			} catch (IOException e) {
				logger.error("", e);
			}
		}

		StringBuilder sb = new StringBuilder();
		String fileExt = EXTENSION_SEPARATOR + InnerFileutils.getFileExtension(multipartFile.getOriginalFilename());

		sb.append( UUID.randomUUID().toString().replace("-", "") ).append(fileExt);
		String saveFileNm = sb.toString();

		sb.setLength(0);
		sb.append(destFilePath).append(FOLDER_SEPARATOR).append(saveFileNm);

		FileVO fileVO = null;

		try {
			byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(sb.toString());

            if ( !path.normalize().startsWith(destFilePath) ) {
                throw new IOException("Invalid file path");
            }

            Files.write(path, bytes);

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
	 * Spring 4 파일 다운로드
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
			downloadlFileNm = getEncodedFileName(request, saveFileNm);
		} else {
			downloadlFileNm = getEncodedFileName(request, orignlFileNm);
		}

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		response.setHeader("Content-Transfer-Encoding", "binary;");

		setContentDisposition(request, response, downloadlFileNm);

		Path source = Paths.get(destFilePath + saveFileNm);

		try ( OutputStream os = response.getOutputStream() ) {
			Files.copy(source, os);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * Spring 4 PDF 파일 열기
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
			downloadlFileNm = getEncodedFileName(request, saveFileNm);
		} else {
			downloadlFileNm = getEncodedFileName(request, orignlFileNm);
		}

		response.setContentType(MediaType.APPLICATION_PDF_VALUE);

		setContentDisposition(request, response, downloadlFileNm);

		Path source = Paths.get(destFilePath + saveFileNm);

		try ( OutputStream os = response.getOutputStream() ) {
			Files.copy(source, os);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 브라우저에 따른 파일명 인코딩 설정 (Content-Disposition 값 생성)
	 * @param request
	 * @param str
	 * @return
	 */
	private static String getEncodedFileName(HttpServletRequest request, String fileName) {
		checkRequest(request);

		if ( ObjectUtils.isEmpty(fileName.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.inValid("fileName"));
		}

		try {
			// RFC 5987 표준 방식 (최신 브라우저 및 Swagger 대응)
			return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
			return fileName;
		}
	}

	/**
	 * 브라우저별로 최적화된 파일 다운로드 응답 헤더를 설정
	 * @param request
	 * @param response
	 * @param fileName
	 */
	private static void setContentDisposition(HttpServletRequest request, HttpServletResponse response, String fileName) {
		String encodedFileName = getEncodedFileName(request, fileName);

		String userAgent = request.getHeader("User-Agent");

		response.setHeader("Content-Transfer-Encoding", "binary");

		// IE / Edge (Trident) 대응
		if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + encodedFileName+ "\"");
		} else {
			// 최신 브라우저: filename* 파라미터 사용 (RFC 5987)
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
		}
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
			if ( ObjectUtils.isEmpty(fileName.trim()) ) {
				throw new IllegalArgumentException(ExceptionMessage.inValid("fileName"));
			}

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
