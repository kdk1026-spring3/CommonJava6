package common.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * Java 7 이상은 불필요
 *  - web.xml 설정
 *  	&ltsession-config&gt
 *  		&ltcookie-config&gt
 *  			&lthttp-only&gttrue&lt/http-only&gt
 *  		&lt/cookie-config&gt
 *  	&lt/session-config&gt
 * </pre>
 */
public class SessionCookieFilter implements Filter {

	@Override
	public void destroy() {
		// Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		/** Cookie Hijacking 방지 */
		String sessionid = request.getSession().getId();
		response.setHeader("Set-Cookie", "JSESSIONID=" + sessionid + "; Path=/; HttpOnly");

		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Auto-generated method stub
	}

}
