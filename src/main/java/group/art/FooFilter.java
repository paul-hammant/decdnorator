package group.art;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FooFilter implements Filter {

    private Renderer r = new Renderer("", "");
    private Selector s = new Selector() {
        public String getPageName(String requestURI) {
            return requestURI;
        }
    };


    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        response.getWriter().write(r.getPage(s.getPageName(requestURI), null));
    }

    public void destroy() {
    }
}
