package group.art.example;

import group.art.Renderer;

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

    private Renderer renderer = new Renderer(ExampleFilter.class, "example/target/classes", "jar/src/test/resources");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String pageName = ((HttpServletRequest) request).getRequestURI().substring(1);
        int ix = pageName.indexOf("?");
        if (ix > 0) {
            pageName = pageName.substring(0, ix);
        }
        // you determine that you should do the cacheable page
        System.err.println("rui: "+ pageName);
        if (pageName.startsWith("has_two_angular_controllers.html")) {
            String page = renderer.getPage(pageName, new HashMap<String, String>());
            response.setContentType("text/html");

            response.getWriter().write(page);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
