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
        // you determine that you should do the cacheable page
        if (true == false) {
            String page = renderer.getPage(requestURI, new HashMap<String, String>());
            response.setContentType("text/html");

            response.getWriter().write(page);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
