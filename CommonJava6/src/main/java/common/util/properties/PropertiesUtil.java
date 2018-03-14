package common.util.properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.util.file.FileUtil;

public class PropertiesUtil {
	
	private PropertiesUtil() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static final String PROP_CLASS_PATH = "properties" + FileUtil.FOLDER_SEPARATOR;
	private static final String PROP_WEB_INF_PATH = "/WEB-INF" +  FileUtil.FOLDER_SEPARATOR + "properties/";
	
	/**
	 * <pre>
	 * Classpath의 Properties 로드
	 *   - properties 파일 / properties xml 파일
	 * </pre> 
	 * @param propFileName
	 * @return
	 */
	public static Properties getPropertiesClasspath(String propFileName) {
		Properties prop = new Properties();
		InputStream is = null;
		String fileNmae = PROP_CLASS_PATH + propFileName;

		try {
			is = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileNmae);
			prop.load(is);
			
		} catch (IOException e) {
			logger.error("getPropertiesClasspath IOException", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				logger.error("getPropertiesClasspath IOException", e);
			}
		}
		
		return prop;
	}
	
	/**
	 * <pre>
	 * WEB-INF의 Properties 로드
	 *   - properties 파일 / properties xml 파일
	 * </pre> 
	 * @param propFileName
	 * @return
	 */
	public static Properties getPropertiesWebInf(String propFileName) {
		Properties prop = new Properties();
		InputStream is = null;
		FileInputStream fis = null;
		String fileNmae = PROP_WEB_INF_PATH + propFileName;
		
		try {
			fis = new FileInputStream(fileNmae); 
			is = new BufferedInputStream(fis);
			prop.load(is);
			
		} catch (IOException e) {
			logger.error("getPropertiesWebInf IOException", e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				logger.error("getPropertiesWebInf IOException", e);
			}
		}
		
		return prop;
	}
	
	/**
	 * <pre>
	 * Properties 생성/덮어쓰기
	 *   - Classpath의 경우 서버 리로드를 해야 반영되므로 생성/덮어쓰기 권장안함
	 * </pre> 
	 * @param type (0: Classpath, 1 : WEB-INF)
	 * @param propFileName
	 * @param prop
	 */
	public static void saveProperties(int type, String propFileName, Properties prop) {
		if ( (prop != null) && (!prop.isEmpty()) ) {
			OutputStream os = null;
			FileOutputStream fos = null;
			
			String fileNmae = "";
			
			switch (type) {
			case 0:
				fileNmae = PROP_CLASS_PATH + propFileName;
				break;
			case 1:
				fileNmae = PROP_WEB_INF_PATH + propFileName;
				break;
			default:
				break;
			}
			
			try {
				fos = new FileOutputStream(fileNmae);
				os = new BufferedOutputStream(fos);
				
				prop.store(os, null);
				
			} catch (IOException e) {
				logger.error("saveProperties IOException", e);
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
					
					if (os != null) {
						os.close();
					}
					
				} catch (IOException e) {
					logger.error("saveProperties IOException", e);
				}
			}				
		}
	}
	
}
