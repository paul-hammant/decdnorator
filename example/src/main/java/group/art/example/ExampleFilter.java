package group.art.example;

import group.art.DecoratorOverrides;
import group.art.Renderer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class ExampleFilter implements Filter {



    private Renderer renderer = new Renderer(ExampleFilter.class, "/classes/", "/");

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String pageName = ((HttpServletRequest) request).getRequestURI().substring(1);
        int ix = pageName.indexOf("?");
        if (ix > 0) {
            pageName = pageName.substring(0, ix);
        }

        final boolean noSecondaryDecoration = request.getParameter("noSecondaryDecoration") != null;

        DecoratorOverrides foo = new DecoratorOverrides() {
            public String override(String decorator, List<String> previouslyDone) {
                if (noSecondaryDecoration == true && previouslyDone.size() > 0) {
                    return NO_DECORATION;
                }
                return decorator;
            }
        };

        // you determine that you should do the cacheable page somehow,
        // rather than fall through to other request mappings.
        if (pageName.startsWith("has_two_angular_controllers.html")) {
            String page = renderer.getPage(foo, pageName, "Greet", "GreetJs", "List", "ListJs");
            response.setContentType("text/html");

            response.getWriter().write(page);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
