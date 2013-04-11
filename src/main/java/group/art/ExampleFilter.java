package group.art;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

public class ExampleFilter implements Filter {

    private Renderer renderer = new Renderer("", "");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        response.getWriter().write(renderer.getPage(requestURI, new HashMap<String, String>()));
    }

    public void destroy() {
    }
}
