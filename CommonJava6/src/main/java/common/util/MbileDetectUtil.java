package common.util;

import javax.servlet.http.HttpServletRequest;

public class MbileDetectUtil {
	
	private MbileDetectUtil() {
		super();
	}

	public static boolean isMobile(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");

		String sPattern1 = ".*(iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*";
		String sPattern2 = ".*(LG|SAMSUNG|Samsung).*";
		
		return userAgent.matches(sPattern1) || userAgent.matches(sPattern2);
	}

}
