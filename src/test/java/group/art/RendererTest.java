package group.art;


import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;

public class RendererTest {

    @Test
    public void fileContentsShouldBeRetrievable() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        ImmutableMap<String, String> build = makeMap().build();
        assertEquals("bar", renderer.getPage("foo.txt", build));
    }

    @Test
    public void simpleHtmlStyleReplacementsShouldBeMade() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        assertEquals("Mary Had A Little Lamb", renderer.getPage("has_replacements.txt", makeMap().put("AA", "Had").put("BB", "Little").build()));
    }

    private ImmutableMap.Builder<String, String> makeMap() {
        return new ImmutableMap.Builder<String, String>();
    }

    @Test
    public void simpleJavaScriptStyleReplacementsShouldBeMade() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        assertEquals("Mary Had A Little Lamb", renderer.getPage("has_replacements_js.txt", makeMap().put("AA", "Had").put("BB", "Little").build()));
    }

    @Test
    public void secondDecoratorShouldBeProcessedForHtmlStyle() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        assertEquals("Mary [Had] A [Little] Lamb", renderer.getPage("has_replacements_and_second_decorator.txt", makeMap().put("AA", "Had").put("BB", "Little").build()));
    }

    @Test
    public void secondDecoratorShouldBeProcessedForJavaScriptStyle() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        assertEquals("Mary [Had] A [Little] Lamb", renderer.getPage("has_replacements_and_second_decorator_js.txt", makeMap().put("AA", "Had").put("BB", "Little").build()));
    }

    @Test
    public void decoratesAngularPageWithTwoControllersIntoOne() throws FileNotFoundException {
        Renderer renderer = new Renderer("target/classes", "src/main/webapp/WEB-INF");
        assertEquals("<!doctype html>\n" +
                "<html ng-app>\n" +
                "  <head>\n" +
                "      <script src=\"http://code.angularjs.org/1.0.5/angular.min.js\"></script>\n" +
                "      <style>\n" +
                "          .ng-scope  {\n" +
                "              border: 1px dashed blue;\n" +
                "              margin: 3px;\n" +
                "          }\n" +
                "      </style>\n" +
                "\n" +
                "    <script>\n" +
                "      function UnifiedCtrl($scope) {\n" +
                "        $scope.name = 'World';\n" +
                "        $scope.names = ['Igor', 'Misko', 'Vojta'];\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Example.com - The worlds best Example website</h1>\n" +
                "    <div ng-controller=\"UnifiedCtrl\">\n" +
                "      Hello {{name}}!\n" +
                "      <ol>\n" +
                "        <li ng-repeat=\"name in names\">{{name}}</li>\n" +
                "      </ol>\n" +
                "    </div>\n" +
                "    <h1>Contact us as /dev/null</h1>\n" +
                "  </body>\n" +
                "</html>\n", renderer.getPage("has_two_angular_controllers.html", makeMap().put("Greet", "").put("GreetJs", "").put("List", "").put("ListJs", "").build()));
    }


}
