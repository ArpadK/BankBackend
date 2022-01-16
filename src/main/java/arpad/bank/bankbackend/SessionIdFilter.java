package arpad.bank.bankbackend;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Both a {@link Filter servlet filter} and a {@link HttpSessionListener session listener} that adds the session id to the logging context (
 * {@link MDC})
 * <p>
 * Requires SLF4J as the logging facade API.
 * <p>
 * With a log management system such as ELK, this will help you in incident analysis, filtering logs from a unique session.
 * <p>
 * By default the sessionId MDC attribute is {@code sessionId} but can be overridden with the Java property {@code slf4j.tools.mdc.sessionId}
 *
 * @author pismy
 */
//TODO: temperory using this implementation. This needs to be adapted to for example work without a serverside session once the login process is implemented
@Component
@Order(1)
public class SessionIdFilter implements Filter, HttpSessionListener {

	private String mdcName;

	public void init(FilterConfig filterConfig) throws ServletException {
		mdcName = filterConfig.getInitParameter("mdc.sessionId") == null ? System.getProperty("slf4j.tools.mdc.sessionId", "sessionId") : filterConfig.getInitParameter("mdc.sessionId");
	}

	private String getConfig(String param, String defaultValue, FilterConfig filterConfig) {
		String valueFromConfig = filterConfig.getInitParameter(param);
		return valueFromConfig != null ? valueFromConfig : System.getProperty("slf4j.tools."+param, defaultValue);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpSession session = ((HttpServletRequest) request).getSession(true);
			if (session != null) {
				// attach to MDC context
				MDC.put(mdcName, session.getId());
			}
		}else{
			System.out.println("not a httpservlet request = " + request.getContentType());
		}

		try {
			chain.doFilter(request, response);
		} finally {
			// detach from MDC context
			MDC.remove(mdcName);
		}
	}

	public void destroy() {
	}

	public void sessionCreated(HttpSessionEvent se) {
		MDC.put(mdcName, se.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		MDC.remove(mdcName);
	}
}
