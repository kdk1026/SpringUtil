package kr.co.test.util.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

	private Spring4FileUtil() {
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

	public static class FileVO implements Serializable {

		private static final long serialVersionUID = 1L;

		public FileVO() {
			super();
		}

		/**
		 * 파일 경로
		 */
		public String destFilePath;

		/**
		 * 파일 확장자
		 */
		public String fileExt;

		/**
		 * 원파일명
		 */
		public String orignlFileNm;

		/**
		 * 저장파일명
		 */
		public String saveFileNm;

		/**
		 * 파일 크기
		 */
		public long fileSize;

		/**
		 * 파일 크기 단위
		 */
		public String fileSizeUnits;

		public String toString() {
			return ToStringBuilder.reflectionToString(
				this, ToStringStyle.MULTI_LINE_STYLE
			);
		}
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
	 */
	public static FileVO uploadFile(MultipartFile multipartFile, String destFilePath) {
		if ( multipartFile == null ) {
			throw new IllegalArgumentException("multipartFile is null");
		}

		if ( StringUtils.hasText(destFilePath) ) {
			throw new IllegalArgumentException("destFilePath is null");
		}

		destFilePath = (destFilePath.replaceAll("^(.*)(.$)", "$2").equals("/")) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);

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

		sb.append( UUID.randomUUID().toString().replaceAll("-", "") ).append(fileExt);
		String saveFileNm = sb.toString();

		sb.setLength(0);
		sb.append(destFilePath).append(FOLDER_SEPARATOR).append(saveFileNm);

		FileVO fileVO = null;

		try {
			byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(sb.toString());
            Files.write(path, bytes);

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
	 * Spring 4 파일 다운로드
	 * @param fileVO
	 * @param request
	 * @param response
	 */
	public static void downloadFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		if ( ObjectUtils.isEmpty(fileVO) ) {
			throw new IllegalArgumentException("fileVO is null");
		}

		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if ( response == null ) {
			throw new IllegalArgumentException("response is null");
		}

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = (destFilePath.replaceAll("^(.*)(.$)", "$2").equals("/")) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);

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

		Path source = Paths.get(destFilePath + saveFileNm);

		try ( OutputStream os = response.getOutputStream() ) {
			Files.copy(source, os);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Spring 4 PDF 파일 열기
	 * @param fileVO
	 * @param request
	 * @param response
	 */
	public static void openPdfFile(FileVO fileVO, HttpServletRequest request, HttpServletResponse response) {
		if ( ObjectUtils.isEmpty(fileVO) ) {
			throw new IllegalArgumentException("fileVO is null");
		}

		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if ( response == null ) {
			throw new IllegalArgumentException("response is null");
		}

		String downloadlFileNm = "";

		String destFilePath = fileVO.destFilePath;
		destFilePath = (destFilePath.replaceAll("^(.*)(.$)", "$2").equals("/")) ? destFilePath : (destFilePath + FOLDER_SEPARATOR);

		String saveFileNm = fileVO.saveFileNm;
		String orignlFileNm = fileVO.orignlFileNm;

		if ( !StringUtils.hasText(orignlFileNm) ) {
			downloadlFileNm = contentDisposition(request, saveFileNm);
		} else {
			downloadlFileNm = contentDisposition(request, orignlFileNm);
		}

		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-Disposition", "inline; attachment; filename=\"" + downloadlFileNm + "\";");

		Path source = Paths.get(destFilePath + saveFileNm);

		try ( OutputStream os = response.getOutputStream() ) {
			Files.copy(source, os);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Spring MultipartFile -> Java File 변환
	 * @param multipart
	 * @return
	 */
	public static File multipartFileToFile(MultipartFile multipartFile) {
		if ( multipartFile == null ) {
			throw new IllegalArgumentException("multipartFile is null");
		}

		File convFile = new File(multipartFile.getOriginalFilename());

		try {
			multipartFile.transferTo(convFile);

		} catch (IllegalStateException | IOException e) {
			logger.error("", e);
		}

        return convFile;
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
		if ( request == null ) {
			throw new IllegalArgumentException("request is null");
		}

		if ( StringUtils.hasText(str) ) {
			throw new IllegalArgumentException("str is null");
		}
		
		String sRes = "";
		String userAgent = request.getHeader("User-Agent");

		try {
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				sRes = URLEncoder.encode(str, StandardCharsets.UTF_8.toString()).replaceAll("\\+", " ");
			} else {
				sRes = new String(str.getBytes(StandardCharsets.UTF_8.toString()), "ISO-8859-1");
			}

		} catch (Exception e) {
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
			if ( StringUtils.hasText(fileName) ) {
				throw new IllegalArgumentException("fileName is null");
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
				throw new IllegalArgumentException("fileSize is invalid");
			}

			if (fileSize == 0) return "0 B";
			String[] units = { "B", "KB", "MB", "GB", "TB" };

			int digitGroups = Math.min((int) (Math.log10(fileSize) / Math.log10(1024)), units.length - 1);
		    return decimalFormat.format(fileSize/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}
	}

}
