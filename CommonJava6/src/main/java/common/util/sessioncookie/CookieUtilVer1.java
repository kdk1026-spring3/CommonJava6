package common.util.sessioncookie;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieUtilVer1 {
	
	private CookieUtilVer1() {
		super();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CookieUtilVer1.class);

	private static final String CHARSET = "UTF-8";

	/**
	 * Servlet 2.5 쿠키 설정
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 * @param maxAge
	 * @param isUseJs
	 */
	public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge, boolean isUseJs) {
		if (isUseJs) {
			Cookie cookie = new Cookie(cookieName, cookieValue);
			cookie.setMaxAge(maxAge);
			cookie.setPath("/");
			
			response.addCookie(cookie);
			
		} else {
			Date expdate = new Date();
			expdate.setTime (expdate.getTime() + (1000 * maxAge));
			DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz", Locale.ENGLISH);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			String cookieExpire = df.format(expdate);
			
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(cookieName);
				sb.append("=");
				sb.append(URLEncoder.encode(cookieValue, CHARSET));
				sb.append("; Expires=");
				sb.append(cookieExpire);
				sb.append("; Path=/");
				sb.append("; HttpOnly");
				
				response.addHeader("Set-Cookie", sb.toString());
				
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
	}

	/**
	 * cookieName 인자 값을 가지는 쿠키의 값 가져오기
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String cookieName) {
		String cookieValue = "";
		Cookie[] cookies = request.getCookies();

		if (cookies != null && cookies.length > 0) {
			for (int i=0; i < cookies.length; i++) {
				if ( cookieName.equals(cookies[i].getName()) ) {
					try {
						cookieValue = URLDecoder.decode(cookies[i].getValue(), CHARSET);
					} catch (UnsupportedEncodingException e) {
						logger.error("", e);
					}
				}
			}
		}
		
		return cookieValue;
	}
	
	/**
	 * 모든 쿠키 제거
	 * @param request
	 * @param response
	 */
	public static void removeCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null && cookies.length > 0) {
			for (int i=0; i < cookies.length; i++) {
				cookies[i].setPath("/");
				cookies[i].setMaxAge(0);
				
				response.addCookie(cookies[i]);
			}
		}
	}

	/**
	 * 특정 쿠키 제거
	 * @param request
	 * @param response
	 * @param cookieName
	 */
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		
		response.addCookie(cookie);
	}

}
