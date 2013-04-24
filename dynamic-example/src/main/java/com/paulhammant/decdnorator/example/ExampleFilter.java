package com.paulhammant.decdnorator.example;

import com.paulhammant.decdnorator.DecorationOverrides;
import com.paulhammant.decdnorator.Decorator;
import com.paulhammant.decdnorator.PathFinder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExampleFilter implements Filter {

    private Decorator decorator = new Decorator(PathFinder.fromClass(ExampleFilter.class).replace("/classes/", "/"));

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String pageName = ((HttpServletRequest) request).getRequestURI().substring(1);
        int ix = pageName.indexOf("?");
        if (ix > 0) {
            pageName = pageName.substring(0, ix);
        }

        final boolean noSecondaryDecoration = request.getParameter("noSecondaryDecoration") != null;

        DecorationOverrides foo = new DecorationOverrides() {
            public String override(String decorator, List<String> previouslyDone) {
                if (noSecondaryDecoration == true && previouslyDone.size() > 0) {
                    return NO_MORE_DECORATION;
                }
                return decorator;
            }
        };

        // you determine that you should do the cacheable page somehow,
        // rather than fall through to other request mappings.
        if (pageName.startsWith("has_two_angular_controllers.html")) {
            String page = decorator.getPage(foo, pageName, "Greet", "GreetJs", "List", "ListJs");
            response.setContentType("text/html");
            ((HttpServletResponse) response).setHeader("TIMEOUT", "TODO");
            response.getWriter().write(page);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
