package com.paulhammant.decdnorator;


import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.paulhammant.decdnorator.DecorationOverrides.NO_DECORATOR_SPECIFIED;
import static com.paulhammant.decdnorator.DecorationOverrides.NO_OVERRIDES;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecoratorTest {

    PathFinder pathFinder = PathFinder.fromClass(DecoratorTest.class).replace("target/classes", "src/test/resources");

    @Test
    public void simpleHtmlStyleReplacementsShouldBeMade() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        Map<String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");
        map.put("AA", "Had");
        assertEquals("Mary Had A Little Lamb", decorator
                .getPage(NO_OVERRIDES, "has_replacements.txt", map));
    }

    @Test
    public void unSpecifiedDecoratorInAChainAllowsOverride() {
        Decorator decorator = new Decorator(pathFinder);
        DecorationOverrides decOvr = mock(DecorationOverrides.class);

        when(decOvr.override(eq(NO_DECORATOR_SPECIFIED),
                eq(newArrayList("has_replacements.txt"))))
                .thenReturn("this_file_does_not_exist");
        try {
            Map<String, String> map = makeMap();
            map.put("AA", "Had");
            map.put("BB", "Little");
            decorator.getPage(decOvr, "has_replacements.txt", map);
            fail("should have barfed");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    private Map<String, String> makeMap() {
        return new HashMap<String, String>();
    }

    @Test
    public void simpleJavaScriptStyleReplacementsShouldBeMade() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        Map <String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");
        assertEquals("Mary Had A Little Lamb", decorator.getPage(NO_OVERRIDES, "has_replacements_js.txt", map));
    }

    @Test
    public void secondDecoratorShouldBeProcessedForHtmlStyle() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        Map <String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");
        assertEquals("Mary [Had] A [Little] Lamb", decorator.getPage(NO_OVERRIDES, "has_replacements_and_second_decorator.txt", map));
    }

    @Test
    public void secondDecoratorShouldBeProcessedForJavaScriptStyle() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        Map <String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");
        assertEquals("Mary [Had] A [Little] Lamb", decorator.getPage(NO_OVERRIDES, "has_replacements_and_second_decorator_js.txt", map));
    }

    @Test
    public void decoratesAngularPageWithTwoControllersIntoOne1000() throws FileNotFoundException {
        long start =  System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            decoratesAngularPageWithTwoControllersIntoOne();
        }
        System.out.println("elapsed:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void decoratesAngularPageWithTwoControllersIntoOne() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        String page = decorator.getPage(NO_OVERRIDES, "has_two_angular_controllers.html", "Greet", "GreetJs", "List", "ListJs");
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
                "      <ol>\n" +
                "        <li ng-repeat=\"name in names\">{{name}}</li>\n" +
                "      </ol>\n" +
                "<!-- yes, we're reordering -->\n" +
                "      Hello {{name}}!\n" +
                "    </div>\n" +
                "    <h1>Contact us as /dev/null</h1>\n" +
                "  </body>\n" +
                "</html>\n", page);
    }

    @Test
    public void secondDecoratorShouldBeOverridable() throws FileNotFoundException {
        DecorationOverrides overrides = new DecorationOverrides.Single("has_replacements.txt", "banana.txt" );

        Decorator decorator = new Decorator(pathFinder);
        Map <String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");
        assertEquals(
                "a\n" +
                "<[Had]>\n" +
                "b\n" +
                "<[Little]>\n" +
                "c",
                decorator.getPage(overrides, "has_replacements_and_second_decorator.txt", map));
    }

    @Test
    public void secondDecoratorShouldBeSkippable() throws FileNotFoundException {
        DecorationOverrides o = new DecorationOverrides() {
            public String override(String decorator, List<String> done) {
                return decorator.replace("has_replacements.txt", DecorationOverrides.NO_MORE_DECORATION);
            }
        };

        Decorator decorator = new Decorator(pathFinder);
        Map <String, String> map = makeMap();
        map.put("AA", "Had");
        map.put("BB", "Little");

        assertEquals(
                "\n" + // decorate-with was here
                "[Had]\n" +
                "this bit goes nowhere\n" +
                "[Little]",
                decorator.getPage(o, "has_replacements_and_second_decorator.txt", map));
    }

    @Test
    public void extractInsertsShouldExtractVariables() throws FileNotFoundException {
        Decorator decorator = new Decorator(pathFinder);
        Map<String, String> vars = decorator.extractInserts("sdkjfhasdkfhjaksdjfh" +
                "qweqwe<!--block:AA-->AaAa<!--endblock:AA-->fghfgh\n" +
                "this bit goes nowhere\n" +
                "werwer<!--block:BB-->BbBb<!--endblock:BB-->dfgdfg\n" +
                "sdkjfhasdkfhjaksdjfh", "AA", "BB");
        assertEquals(2, vars.size());
        assertEquals("AaAa", vars.get("AA"));
        assertEquals("BbBb", vars.get("BB"));
    }

}
