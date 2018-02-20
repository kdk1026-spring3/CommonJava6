package common.spring.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import common.util.ResponseUtil;
import common.util.file.FileTypeUtil;
import common.util.file.FileUtil;

public class SpringFileUtil {
	
	private SpringFileUtil() {
		super();
	}

	private static final Logger logger = LoggerFactory.getLogger(SpringFileUtil.class);

	private static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * <pre>
	 * Spring 전용 파일 업로드
	 *  - Commons FileUpload 사용
	 *  - 확장자 체크 및 MIME Type 체크 선행 후 진행 권장
	 * </pre>
	 * @param multipartFile
	 * @param strDestFilePath
	 * @return
	 */
	public static Map<String, Object> uploadFileSpring(MultipartFile multipartFile, String strDestFilePath) {
		Map<String, Object> fileInfoMap = new HashMap<String, Object>();

		File destFilePath = new File(strDestFilePath);
		if (!destFilePath.exists()) {
			destFilePath.mkdirs();
		}

		String saveFileName = getRandomString();
		String fileExtension = FileUtil.getFileExtension(multipartFile.getOriginalFilename());

		try {
			File targetFile = new File(strDestFilePath + File.separator + saveFileName + "." + fileExtension);
			multipartFile.transferTo(targetFile);
			
		} catch (Exception e) {
			logger.error("uploadFileSpring Exception", e);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(saveFileName).append(".").append(fileExtension);

		String fileUnitSize = FileUtil.readableFileSize(multipartFile.getSize());

		fileInfoMap.put("originalFilename", multipartFile.getOriginalFilename());
		fileInfoMap.put("saveFileName", sb.toString());
		fileInfoMap.put("fileSize", multipartFile.getSize());
		fileInfoMap.put("fileUnitSize", fileUnitSize);

		return fileInfoMap;
	}
	
	/**
	 * Spring 전용 파일 다운로드
	 * @param filePath
	 * @param originalFilename
	 * @param saveFileName
	 * @param request
	 * @param response
	 */
	public static void downloadFileSpring(String filePath, String originalFilename, String saveFileName,
			HttpServletRequest request, HttpServletResponse response) {

		String originFileName = "";
		if ( StringUtils.isEmpty(originalFilename) ) {
			originFileName = ResponseUtil.contentDisposition(request, saveFileName);
		} else {
			originFileName = ResponseUtil.contentDisposition(request, originalFilename);
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Transfer-Encoding", "binary;");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + originFileName + "\";");

		FileInputStream fis = null;
		InputStream is = null;
		OutputStream os = null;
		
		try {
			fis = new FileInputStream(filePath + File.separator + saveFileName);
			is = new BufferedInputStream(fis);
			os = response.getOutputStream();

			FileCopyUtils.copy(is, os);
			
		} catch (Exception e) {
			logger.error("downloadFileSpring Exception", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e.getClass().getSimpleName());
					logger.debug(e.getCause().getMessage());
				}
			}
		}
	}
	
	/**
	 * Spring MultipartFile -> Java File 변환
	 * @param multipart
	 * @return
	 */
	public static File multipartToFile(MultipartFile multipart) {
		File convFile = new File(multipart.getOriginalFilename());
		
		try {
			multipart.transferTo(convFile);
			
		} catch (IllegalStateException e) {
			logger.error("multipartToFile IllegalStateException", e);
		} catch (IOException e) {
			logger.error("multipartToFile IOException", e);
		}
		
        return convFile;
	}
	
	public static boolean isDocImageFile(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		
		String sExtension = FileUtil.getFileExtension(fileName);
		String sMimeType = file.getContentType();

		return FileTypeUtil.isDocFile(sExtension, sMimeType) || FileTypeUtil.isImgFile(sExtension, sMimeType);
	}

	public static boolean isDocFile(MultipartFile file) {
		String fileName = file.getName();
		
		String sExtension = FileUtil.getFileExtension(fileName);
		String sMimeType = file.getContentType();

		return FileTypeUtil.isDocFile(sExtension, sMimeType);
	}

	public static boolean isImageFile(MultipartFile file) {
		String fileName = file.getName();
		
		String sExtension = FileUtil.getFileExtension(fileName);
		String sMimeType = file.getContentType();

		return FileTypeUtil.isImgFile(sExtension, sMimeType);
	}
	
	/**
	 * 텍스트 내용을 행당 경로에 파일로 생성
	 * @param filePath
	 * @param text
	 * @param encoding
	 */
	public static void writeFile(String filePath, String text, String encoding) {
		File file = new File(filePath);
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(file);
			
			StreamUtils.copy(text, Charset.forName(encoding), fos);
			
		} catch (Exception e) {
			logger.error("writeFile Exception", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("writeFile IOException", e);
				}
			}
		}		
	}
	
	/**
	 * 파일을 텍스트로 읽음
	 * @param file
	 * @return
	 */
	public static String readFile(File file, String encoding) {
		String content = "";
		byte[] bData = null;
		
		try {
			bData = FileCopyUtils.copyToByteArray(file);
		} catch (IOException e) {
			logger.error("readFile IOException", e);
		}
		
		try {
			if (bData != null) {
				content = new String(bData, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("readFile UnsupportedEncodingException", e);
		}
		
		return content;
	}
	
	/**
	 * 파일 복사
	 * @param srcFile
	 * @param destFile
	 */
	public static void copyFile(String srcFilePath, String destFilePath) {
		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);
		
		try {
			FileCopyUtils.copy(srcFile, destFile);
		} catch (IOException e) {
			logger.error("copyFile IOException", e);
		}
	}
	
}
